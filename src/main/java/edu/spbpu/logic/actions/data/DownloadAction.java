package edu.spbpu.logic.actions.data;

import edu.spbpu.models.old_data_accessors.DataSeries;
import edu.spbpu.models.old_data_accessors.DataSet;
import edu.spbpu.logic.Action;
import edu.spbpu.util.Downloader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vasiliy Bobkov on 20.01.2017.
 */
public class DownloadAction implements Action{
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, DataSet> dataSetMap = (HashMap<String, DataSet>) request.getServletContext().getAttribute("dataSetMap");
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
        Downloader downloader = (Downloader) request.getServletContext().getAttribute("downloader");
        dataSeries.startDownloadData(downloader);

        HttpSession session = request.getSession();
        ArrayList<DataSeries> dataSeriesArrayList = (ArrayList<DataSeries>) session.getAttribute("arrayOfDataSet");
        if (dataSeriesArrayList == null) {
            dataSeriesArrayList = new ArrayList<>();
            session.setAttribute("arrayOfDataSet", dataSeriesArrayList);
        }
        dataSeriesArrayList.add(dataSeries);
        return "/data/my";
    }
}