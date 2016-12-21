package util;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
@Getter
public class DataParser {

    private LocalDateTime timeStart = null;
    private int timeResolution = 0;
    private LocalDateTime timeEnd = null;

    public Map<LocalDateTime, Double> parseToMap(String text) {
        parseTimeStart(text);
        parseTimeResolution(text);
        Map<LocalDateTime, Double> data = new TreeMap<>();
        LocalDateTime time = null;
        Pattern pattern = Pattern.compile("\\[(\\d+)\\]\\[\\d\\], (-*\\d+.\\d*E*.\\d*)");
        Matcher matcher = pattern.matcher(text);
        double value;

        while (matcher.find()) {
            assert timeStart != null;
            System.out.println(timeResolution);
            time = timeStart.plusHours(timeResolution * Long.parseLong(matcher.group(1)));
            value = Double.parseDouble(matcher.group(2));
            System.out.println(time+" "+value);
            data.put(time, value);
        }
        timeEnd = time;

        return data;
    }

    private void parseTimeStart(String text) {
        Pattern timePattern = Pattern.compile("time, \\[\\d+\\]\\D*(\\d+.\\d+)");
        Matcher matcher = timePattern.matcher(text);
        if (matcher.find()) {
            timeStart = getTimeFromDouble(Double.parseDouble(matcher.group(1)));
        }
    }


    private void parseTimeResolution(String text) {
        Pattern timePattern = Pattern.compile("time, \\[\\d+\\]\\D*(\\d+.\\d+), (\\d+.\\d+)");
        Matcher matcher = timePattern.matcher(text);
        if (matcher.find()) {
            timeResolution = (int) Math.round((Double.parseDouble(matcher.group(2))-Double.parseDouble(matcher.group(1)))*24);
        }
    }

    private LocalDateTime getTimeFromDouble(double index) {
        LocalDateTime date = LocalDateTime.of(0, 12, 29, 23, 0);
        date = date.plusDays((long) index).plusHours(Math.round(24*(index -(long) index)));
        return date;
    }
}