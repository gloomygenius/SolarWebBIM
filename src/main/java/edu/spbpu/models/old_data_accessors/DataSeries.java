package edu.spbpu.models.old_data_accessors;

import edu.spbpu.exceptions.DownloadException;
import lombok.Builder;
import lombok.Getter;
import edu.spbpu.util.DataParser;
import edu.spbpu.util.Downloader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
@Builder

public class DataSeries {
    private DataSet dataSet;
    private String parameter;
    private Double longitude;
    private Double latitude;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private Map<LocalDateTime, Double> dataMap;

    public String getLink(){
        return dataSet.getLink(this);
    }
    public void startDownloadData(Downloader downloader){
        Thread thread = new Thread(() -> {
            try {
                dataMap=new DataParser().parseToMap(downloader.getData(getLink()));
            } catch (DownloadException e) {
                e.printStackTrace();
            }
        });
        thread.run();
    }
    public String convertToCSV() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<LocalDateTime, Double> entry : dataMap.entrySet()) {
            builder
                    .append(dateFormat.format(entry.getKey()))
                    .append(";")
                    .append(entry.getValue())
                    .append("\r\n");
        }
        return builder.toString();
    }
}