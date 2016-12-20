package listeners;

import lombok.extern.log4j.Log4j;
import util.Downloader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@WebListener
@Log4j
public class Initializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String pathToResources = context.getRealPath("/") + "WEB-INF/classes/";
        String pathToNasaAuth = pathToResources + "nasa.properties";
        try (FileInputStream fileInputStream = new FileInputStream(pathToNasaAuth);
             BufferedInputStream stream = new BufferedInputStream(fileInputStream)) {
            Properties properties = new Properties();
            properties.load(stream);
            String login = properties.getProperty("login");
            String password = properties.getProperty("password");
            Downloader downloader = new Downloader(login,password);
            context.setAttribute("downloader",downloader);
        } catch (IOException e) {
            log.error(e);
        }
    }
}