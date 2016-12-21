package servlets;

import models.DataSeries;
import models.DataSet;
import util.Downloader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@WebServlet("/data/download")
public class NasaDownloaderServlet extends CommonHttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap<String, DataSet> dataSetMap = (HashMap<String, DataSet>) getServletContext().getAttribute("dataSetMap");
        String dataSetName = request.getParameter("dataSetInfo");

        DataSeries.DataSeriesBuilder dataSeriesBuilder = DataSeries.builder();

        DataSet dataSet = dataSetMap.get(dataSetName);
        dataSeriesBuilder.dataSet(dataSet);

        String parameter = request.getParameter("parameter");
        dataSeriesBuilder.parameter(parameter);

        Double latitude = Double.parseDouble(request.getParameter("latitude"));
        dataSeriesBuilder.latitude(latitude);

        Double longitude = Double.parseDouble(request.getParameter("longitude"));
        dataSeriesBuilder.longitude(longitude);

        LocalDateTime timeStart = LocalDateTime.parse(request.getParameter("timeStart"));
        dataSeriesBuilder.timeStart(timeStart);

        LocalDateTime timeEnd = LocalDateTime.parse(request.getParameter("timeEnd"));
        dataSeriesBuilder.timeEnd(timeEnd);

        DataSeries dataSeries = dataSeriesBuilder.build();
        Downloader downloader = (Downloader) getServletContext().getAttribute("downloader");
        dataSeries.startDownloadData(downloader);

        HttpSession session = request.getSession();
        ArrayList<DataSeries> dataSeriesArrayList = (ArrayList<DataSeries>) session.getAttribute("arrayOfDataSet");
        if (dataSeriesArrayList == null) {
            dataSeriesArrayList = new ArrayList<>();
            session.setAttribute("arrayOfDataSet", dataSeriesArrayList);
        }
        dataSeriesArrayList.add(dataSeries);

        response.sendRedirect("/data/my");
    }
}