package edu.spbpu.listeners;

import edu.spbpu.dao.*;
import edu.spbpu.dao.fileImpl.FileDAOFactory;
import edu.spbpu.logic.services.InverterService;
import edu.spbpu.logic.services.PowerStationService;
import edu.spbpu.logic.services.SolarModuleService;
import edu.spbpu.models.old_data_accessors.DataSet;
import edu.spbpu.util.Downloader;
import lombok.extern.log4j.Log4j;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static edu.spbpu.Constants.*;


@Log4j
@WebListener
public class Initializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("start Initializer");
        ServletContext context = sce.getServletContext();
        String pathToResources = context.getRealPath("/") + "WEB-INF/classes/";
        String pathToNasaAuth = pathToResources + "nasa.properties";
        DAOAbstractFactory abstractFactory = new FileDAOFactory(context.getRealPath("/") + "WEB-INF/classes/datasheets", "E:\\MERRA 2\\result");
        Map<String, BaseDAO> daoMap = new HashMap<>();
        LocalDataDAO localDataDAO = abstractFactory.createLocalDataDAO();
        daoMap.put(LOCAL_DATA_DAO, localDataDAO);
        context.setAttribute(LOCAL_DATA_DAO, localDataDAO);

        SolarModuleDAO solarModuleDAO = abstractFactory.createSolarModuleDAO();
        context.setAttribute(SOLAR_MODULE_DAO, solarModuleDAO);
        daoMap.put(SOLAR_MODULE_DAO, solarModuleDAO);
        context.setAttribute(SOLAR_MODULE_SERVICE, new SolarModuleService(solarModuleDAO));

        InverterDAO inverterDAO = abstractFactory.createInverterDAO();
        context.setAttribute(INVERTER_DAO, inverterDAO);
        daoMap.put(INVERTER_DAO, inverterDAO);
        context.setAttribute(INVERTER_SERVICE, new InverterService(inverterDAO));

        PowerStationDAO powerStationDAO = abstractFactory.createPowerStationDAO();
        context.setAttribute(POWER_STATION_DAO, powerStationDAO);
        daoMap.put(POWER_STATION_DAO, powerStationDAO);
        context.setAttribute(POWER_STATION_SERVICE, new PowerStationService(daoMap));

        try (FileInputStream fileInputStream = new FileInputStream(pathToNasaAuth);
             BufferedInputStream stream = new BufferedInputStream(fileInputStream)) {
            Properties properties = new Properties();
            properties.load(stream);
            String login = properties.getProperty("login");
            String password = properties.getProperty("password");
            Downloader downloader = new Downloader(login, password);
            context.setAttribute("downloader", downloader);
        } catch (IOException e) {
            log.error(e);
        }
        String pathToDataSetList = pathToResources + "data_set/data_set_names.properties";
        try (FileInputStream fileInputStream = new FileInputStream(pathToDataSetList);
             BufferedInputStream biStream = new BufferedInputStream(fileInputStream)) {
            Properties properties = new Properties();
            properties.load(biStream);
            String[] dataSetNames = properties.getProperty("names").split(",");
            Stream<String> stream = Stream.of(dataSetNames);
            HashMap<String, DataSet> dataSetMap = new HashMap<>();
            stream.forEach(s -> dataSetMap.put(s.trim(), getDataSetFromProperties(
                    pathToResources + "data_set/" + s.trim() + ".properties")));
            context.setAttribute("dataSetMap", dataSetMap);
        } catch (IOException e) {
            log.error(e);
        }
    }

    private DataSet getDataSetFromProperties(String dataSetPath) {
        try (FileInputStream fileInputStream = new FileInputStream(dataSetPath);
             BufferedInputStream stream = new BufferedInputStream(fileInputStream)) {

            Properties properties = new Properties();
            properties.load(stream);

            DataSet.DataSetBuilder dataSetBuilder = DataSet.builder();
            dataSetBuilder.dataSource(properties.getProperty("dataSource"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime defaultTime = LocalDateTime.parse(properties.getProperty("defaultTime"), formatter);
            dataSetBuilder.defaultTime(defaultTime);

            String parameters = properties.getProperty("parameters");
            Set<String> parameterSet = new HashSet<>();
            for (String s : parameters.split(",")) {
                s = s.trim();
                parameterSet.add(s);
            }
            dataSetBuilder.parameters(parameterSet)
                    .dataSetName(properties.getProperty("dataSet"))
                    .timeResolution(Double.parseDouble(properties.getProperty("timeResolution")))
                    .minLongitude(Double.parseDouble(properties.getProperty("minLongitude")))
                    .maxLongitude(Double.parseDouble(properties.getProperty("maxLongitude")))
                    .degreeLon(Double.parseDouble(properties.getProperty("degreeLon")))
                    .minLatitude(Double.parseDouble(properties.getProperty("minLatitude")))
                    .maxLatitude(Double.parseDouble(properties.getProperty("maxLatitude")))
                    .degreeLat(Double.parseDouble(properties.getProperty("degreeLat")));
            return dataSetBuilder.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}