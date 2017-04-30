package edu.spbpu.models.old_data_accessors;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
public class DataSet {
    private String dataSource;
    private String dataSetName;
    private LocalDateTime defaultTime;
    private Set<String> parameters;
    private double timeResolution;
    private double minLongitude;
    private double maxLongitude;
    private double degreeLon;
    private double minLatitude;
    private double maxLatitude;
    private double degreeLat;

    public String getLink(DataSeries dataSeries) {
        return "https://" +
                dataSource + ".sci.gsfc.nasa.gov/dods/" +
                dataSetName + ".ascii?" +
                dataSeries.getParameter() +
                "[" + generateTimeIndex(dataSeries.getTimeStart()) + ":" +
                generateTimeIndex(dataSeries.getTimeEnd()) + "]" + "[" +
                generateLatIndex(dataSeries.getLatitude()) + "]" + "[" +
                generateLonIndex(dataSeries.getLongitude()) + "]";
    }

    private int generateTimeIndex(LocalDateTime time) {
        int days = time.getDayOfYear();
        int year = time.getYear();
        while (year > defaultTime.getYear()) {
            days += getYearDays(year - 1);
            year--;
        }
        int dayMultiplier = (int) Math.round(1 / timeResolution);
        int hourMultiplier = (int) Math.round(24 * timeResolution);
        return (days - defaultTime.getDayOfYear()) * dayMultiplier + Math.round(time.getHour() / hourMultiplier);
    }

    private int getYearDays(int year) {
        if (year % 4 == 0) return 366;
        return 365;
    }

    private int generateLatIndex(double lat) {
        return (int) Math.round((lat - minLatitude) / degreeLat);
    }

    private int generateLonIndex(double lon) {
        return (int) Math.round((lon - minLongitude) / degreeLon);
    }
}