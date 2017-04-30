package edu.spbpu.dao.fileImpl;

import edu.spbpu.dao.*;
import lombok.AllArgsConstructor;

import java.io.File;

/**
 * Created by Vasiliy Bobkov on 29.03.2017.
 */
@AllArgsConstructor
public class FileDAOFactory implements DAOAbstractFactory {
    private String dataSheetPath = new File("resources/datasheets/").getAbsolutePath();
    private String localDataPath = "E:\\MERRA 2\\result";

    @Override
    public LocalDataDAO createLocalDataDAO() {
        return new LocalDataDAOImpl(localDataPath);
    }

    @Override
    public SolarModuleDAO createSolarModuleDAO() {
        return new SolarModuleDAOImpl(dataSheetPath + "/PV module catalog.csv");
    }

    @Override
    public PowerStationDAO createPowerStationDAO() {
        return new PowerStationDAOImpl();
    }

    @Override
    public InverterDAO createInverterDAO() {
        return new InverterDAOimpl(dataSheetPath + "/CEC Inverters.csv");
    }
}