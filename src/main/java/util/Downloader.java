package util;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import exceptions.DownloadException;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class Downloader {
    private static final Logger log = Logger.getLogger(Downloader.class);
    private String login;
    private String password;

    public String getData(String link) throws DownloadException {

        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        try {
            return getResponseFromURL(
                    getLinkFromResponse(
                            getResponseFromURL(
                                    getResponseFromURL(link)
                            )));
        } catch (IOException e) {
            throw new DownloadException(e);
        }
    }

    private String getAuthResponse(String address) throws IOException {
        log.info("Открываем авторизованное соединение: " + address);
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(60 * 1000);
        connection.setConnectTimeout(60 * 1000);
        String authorization = login + ":" + password;
        String encodedAuth = "Basic " + Base64.encode(authorization.getBytes());
        connection.setRequestProperty("Authorization", encodedAuth);
        return getTextResponse(connection);
    }

    private String getLinkFromResponse(String responce) throws DownloadException {
        log.info("Парсим ответ");
        Pattern pattern = Pattern.compile("a href=\\\"(.+)\\\"");
        Matcher matcher = pattern.matcher(responce);
        String link;
        if (matcher.find()) {
            link = matcher.group(1);
        } else {
            throw new DownloadException("Responce can not be parsed");
        }
        return link;
    }

    private String getResponseFromURL(String url) throws IOException, DownloadException {

        log.info("Открываем соединение: " + url);
        URL object = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) object.openConnection();
        connection.setReadTimeout(300 * 1000);
        connection.setConnectTimeout(300 * 1000);

        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 200:
                log.info("Ответ сервера 200");
                log.info("Файл успешно загружен");
                return getTextResponse(connection);
            case 302:
                log.info("Ответ сервера 302");
                return (getLinkFromResponse(getTextResponse(connection)));
            case 401:
                log.info("Ответ сервера 401");
                return getAuthResponse(url);
            default:
                log.info("Ответ сервера " + responseCode);
                return String.valueOf(responseCode);

        }
    }

    private String getTextResponse(HttpURLConnection connection) {
        StringBuilder builder = new StringBuilder();

        String encoding = connection.getContentEncoding() == null ? "UTF-8"
                : connection.getContentEncoding();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream(), encoding))) {
            String nextString;
            while ((nextString = reader.readLine()) != null) {
                builder.append(nextString).append("\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}