package models;

import exceptions.DownloadException;
import lombok.Builder;
import lombok.Getter;
import util.DataParser;
import util.Downloader;

import java.time.LocalDateTime;
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
}