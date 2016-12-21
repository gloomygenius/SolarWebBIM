package listeners;

import lombok.extern.log4j.Log4j;
import models.DataSet;
import util.Downloader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

@Log4j
@WebServlet("/init")
public class Initializer extends HttpServlet implements ServletContextListener {
    private boolean isInited=false;
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isInited) {
            initContext();
            isInited=true;
        }
        response.sendRedirect("/");
    }

//    @Override
//    public void contextInitialized(ServletContextEvent sce) {
//        log.debug("start Initializer");
//        ServletContext context = sce.getServletContext();
//        String pathToResources = context.getRealPath("/") + "WEB-INF/classes/";
//        String pathToNasaAuth = pathToResources + "nasa.properties";
//        try (FileInputStream fileInputStream = new FileInputStream(pathToNasaAuth);
//             BufferedInputStream stream = new BufferedInputStream(fileInputStream)) {
//            Properties properties = new Properties();
//            properties.load(stream);
//            String login = properties.getProperty("login");
//            String password = properties.getProperty("password");
//            Downloader downloader = new Downloader(login, password);
//            context.setAttribute("downloader", downloader);
//        } catch (IOException e) {
//            log.error(e);
//        }
//        String pathToDataSetList = pathToResources + "data_set/data_set_names.properties";
//        try (FileInputStream fileInputStream = new FileInputStream(pathToDataSetList);
//             BufferedInputStream biStream = new BufferedInputStream(fileInputStream)) {
//            Properties properties = new Properties();
//            properties.load(biStream);
//            String[] dataSetNames = properties.getProperty("names").split(",");
//            Stream<String> stream = Stream.of(dataSetNames);
//            stream.forEach(s -> s = s.trim());
//            HashMap<String, DataSet> dataSetMap=new HashMap<>();
//            stream.forEach(s -> dataSetMap.put(s, getDataSetFromProperties(
//                    pathToResources + "data_set/" + s + ".properties")));
//            context.setAttribute("dataSetMap",dataSetMap);
//        } catch (IOException e) {
//            log.error(e);
//        }
//    }

private void initContext(){
    log.info("start Initializer");
    ServletContext context = getServletContext();
    String pathToResources = context.getRealPath("/") + "WEB-INF/classes/";
    String pathToNasaAuth = pathToResources + "nasa.properties";
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
        HashMap<String, DataSet> dataSetMap=new HashMap<>();
        stream.forEach(s -> dataSetMap.put(s.trim(), getDataSetFromProperties(
                pathToResources + "data_set/" + s.trim() + ".properties")));
        context.setAttribute("dataSetMap",dataSetMap);
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
            for (String s: parameters.split(",")) {
                s=s.trim();
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
            return  dataSetBuilder.build();
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }
}