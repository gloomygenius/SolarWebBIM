package edu.spbpu.logic.actions.project;

import edu.spbpu.dao.*;
import edu.spbpu.dao.fileImpl.FileDAOFactory;
import edu.spbpu.models.Inverter;
import edu.spbpu.models.LocalData;
import edu.spbpu.models.Position;
import edu.spbpu.models.PowerStation;
import lombok.SneakyThrows;

import java.time.ZoneId;

import static edu.spbpu.dao.LocalDataDAO.LocalDataType.*;

/**
 * Created by Vasiliy Bobkov on 31.03.2017.
 */
public class Optimizer {
    @SneakyThrows
    private static void calculate(PowerStation powerStation) {
        DAOAbstractFactory factory = new FileDAOFactory("C:\\Users\\Василий\\IdeaProjects\\SolarWebBIM\\src\\main\\resources\\datasheets", "E:\\MERRA 2\\result");
        SolarModuleDAO solarModuleDAO = factory.createSolarModuleDAO();
        InverterDAO inverterDAO = factory.createInverterDAO();
        LocalDataDAO localDataDAO = factory.createLocalDataDAO();

        Inverter inverter = inverterDAO.getByName("Sungrow Power Supply: SG1000MX 630V [CEC 2016]").orElseThrow(() -> new RuntimeException("Нет такого инвертора в базе"));
        Position position = powerStation.getPosition();
        LocalData directRadiation = localDataDAO.getByCoordinate(position, GLOBAL_RADIATION);
        LocalData windSpeedData2m = localDataDAO.getByCoordinate(position, WIND_2M);
        LocalData ambientTemperature = localDataDAO.getByCoordinate(position, TEMPERATURE);
        powerStation.setDirectRadiation(directRadiation);
        powerStation.setWindSpeedData(windSpeedData2m);
        powerStation.setAmbientTemperature(ambientTemperature);

        powerStation.setInverter(inverter);

        int inverterAmount = (int) Math.ceil(powerStation.getNominalPower() * 1E6 / inverter.getP_ac0());
        System.out.println("inverter amountOfPaneles:" + inverterAmount);
        powerStation.setInverterAmount(inverterAmount);

        int ISstringAmount = inverterAmount * inverter.getConnections();
        powerStation.setStringAmount(ISstringAmount);
        powerStation.setRowsOfPanel(2);
    }

    public static void main(String[] args) {
        Position krasnodar = new Position(45, 39, 25);
        PowerStation powerStation = PowerStation.builder()
                .nominalPower(10000000D)
                .name("Краснодарская СЭС")
                .id(1)
                .position(krasnodar)
                .timeZone(ZoneId.of("UTC+3"))
                .build();
        calculate(powerStation);
    }

//    @Override
//    public String execute(HttpServletRequest request, HttpServletResponse response) {
//        PowerStationDAO powerStationDAO = (PowerStationDAO) request.getServletContext().getAttribute(POWER_STATION_DAO);
//        long id = Long.parseLong(request.getParameter("station_id"));
//        PowerStation station = powerStationDAO.read(id)
//                .orElseThrow(() -> new RuntimeException("Station with id " + id + "does't exist"));
//        calculate(station);
//        return null;
//    }
}
