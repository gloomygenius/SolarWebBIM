package edu.spbpu.util;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import edu.spbpu.exceptions.DownloadException;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
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
            return getResponseFromURL(link);
        } catch (IOException e) {
            throw new DownloadException(e);
        }
    }

    private String getAuthResponse(String address) throws IOException, DownloadException {
        log.info("Opening auth connection: " + address);
        URL url = new URL(address);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection instanceof HttpsURLConnection)
            ((HttpsURLConnection)connection).setHostnameVerifier(new NullHostNameVerifier());
        connection.setReadTimeout(60 * 1000);
        connection.setConnectTimeout(60 * 1000);
        String authorization = login + ":" + password;
        String encodedAuth = "Basic " + Base64.encode(authorization.getBytes());
        connection.setRequestProperty("Authorization", encodedAuth);
        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 200:
                log.info("Response code 200");
                log.info("Файл успешно загружен");
                return getTextResponse(connection);
            case 301:
                log.info("Response code 301");
                return (getResponseFromURL(getLinkFromResponse(getTextResponse(connection))));
            case 302:
                log.info("Response code 302");
                return (getResponseFromURL(getLinkFromResponse(getTextResponse(connection))));
            default:
                log.info("Response code " + responseCode);
                return String.valueOf(responseCode);
        }
    }

    private String getLinkFromResponse(String responce) throws DownloadException {
        log.info("Parsing response...");
        Pattern pattern = Pattern.compile("a href=\\\"(.+)\\\"");
        Matcher matcher = pattern.matcher(responce);
        String link;
        if (matcher.find()) {
            link = matcher.group(1);
        } else {
            throw new DownloadException("Response can not be parsed:"+responce);
        }
        return link;
    }

    private String getResponseFromURL(String url) throws IOException, DownloadException {

        log.info("Opening auth connection: " + url);
        URL object = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) object.openConnection();
        if (connection instanceof HttpsURLConnection)
            ((HttpsURLConnection)connection).setHostnameVerifier(new NullHostNameVerifier());
        connection.setReadTimeout(300 * 1000);
        connection.setConnectTimeout(300 * 1000);

        int responseCode = connection.getResponseCode();
        switch (responseCode) {
            case 200:
                log.info("Response code 200");
                log.info("Файл успешно загружен");
                return getTextResponse(connection);
            case 301:
                log.info("Response code 301");
                return (getResponseFromURL(getLinkFromResponse(getTextResponse(connection))));
            case 302:
                log.info("Response code 302");
                return (getResponseFromURL(getLinkFromResponse(getTextResponse(connection))));
            case 401:
                log.info("Response code 401");
                return getAuthResponse(url);
            default:
                log.info("Response code " + responseCode);
                return String.valueOf(responseCode);

        }
    }

    private String getTextResponse(HttpURLConnection connection) throws DownloadException {
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
            throw new DownloadException(e);
        }
        return builder.toString();
    }
}

class NullHostNameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}