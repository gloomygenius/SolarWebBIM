package edu.spbpu.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import edu.spbpu.util.LineChartNumber;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.JulianFields;
import java.time.temporal.TemporalField;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static java.lang.Math.*;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class SunPosition {
    private double azimuth;
    private double elevation;

    public SunPosition(Position position, Instant instant) {
        init(position,instant);
    }
    private void init(Position position, Instant instant){
        double julianDay = instantToJulianDate(instant);
//        double d = julianDay - 2451543.5;
        double d = julianDay - 2451545;
        double w = 282.9404 + 4.70935e-5 * d; //долгота Перигелия
//        double w = 462.94719; //долгота Перигелия по википедии
        double eccentr = 0.016709 - 1.151E-9 * d; //эксцентриситет
        double M = (356.0470 + 0.9856002585 * d) % 360; // средняя аномалия
        double meanSunLongitude = w + M;   // Средняя долгота солнца
        double oblecl = 23.4393 - 3.563E-7 * d; //Sun's obliquity of the ecliptic
        //вспомогательный угол
        double E = M + (180 / PI) * eccentr * sin(M * PI / 180) * (1 + eccentr * cos(M * PI / 180));
        //Прямоугольные координаты в плане
        double x = cos(E * PI / 180) - eccentr;
        double y = sin(E * PI / 180) * sqrt(1 - eccentr * eccentr);

        double r = sqrt(x * x + y * y);
        double v = atan2(y, x) * 180 / PI;
        double sunLongitude = v + w; //долгота солнца
        //эклиптические прямоугольные координаты
        double xEclip = r * cos(sunLongitude * PI / 180);
        double yEclip = r * sin(sunLongitude * PI / 180);
        double zEclip = 0;

        //rotate to equatorial coord:
        double xEquat = xEclip;
        double yEquat = yEclip * cos(oblecl * PI / 180) + zEclip * sin(oblecl * PI / 180);
        double zEquat = yEclip * sin(23.4406 * PI / 180) + zEclip * cos(oblecl * PI / 180);

        //convert equatorial rectangular coordinates to RA and Decl:
        r = sqrt(pow(xEquat, 2) + pow(yEquat, 2) + pow(zEquat, 2)) - (position.getAltitude() / 149598000);
        double RA = atan2(yEquat, xEquat) * 180 / PI;
        double delta = asin(zEquat / r) * 180 / PI;


        //Calculate local siderial time
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
        double UTH = zonedDateTime.getHour() + zonedDateTime.getMinute() / 60 + zonedDateTime.getSecond() / 3600;
        double GMST0 = ((meanSunLongitude + 180) % 360) / 15.0;
        double SIDTIME = GMST0 + UTH + position.getLongitude() / 15.0;

        //Replace RA with hour angle HA
        double HA = (SIDTIME * 15 - RA);
        //convert to rectangular position system
        x = cos(HA * (PI / 180)) * cos(delta * (PI / 180));
        y = sin(HA * (PI / 180)) * cos(delta * (PI / 180));
        double z = sin(delta * (PI / 180));
        //rotate this along an axis going east-west.
        double latitude = position.getLatitude();
        double xHor = x * cos((90 - latitude) * (PI / 180)) - z * sin((90 - latitude) * (PI / 180));
        double yHor = y;
        double zHor = x * sin((90 - latitude) * (PI / 180)) + z * cos((90 - latitude) * (PI / 180));
        //Find the h and AZ
        azimuth = atan2(yHor, xHor) * (180 / PI) + 180;
        elevation = asin(zHor) * (180 / PI);
    }

    private static double instantToJulianDate(Instant instant) {
        LocalDateTime dateTime = LocalDateTime
                .ofInstant(instant, ZoneId.of("UTC"));
        return JulianFields.JULIAN_DAY.getFrom(dateTime)
                + (dateTime.getHour() + dateTime.getMinute() / 60.0 + dateTime.getSecond() / 3600.0) / 24.0;
    }

    public static void main(String[] args) {
        ZonedDateTime startTime = ZonedDateTime.of(2016, 1, 1, 13, 0, 0, 0, ZoneId.of("UTC+3"));
        IntStream.range(0, 24)
                .mapToObj(startTime::plusHours)
                .peek(System.out::print)
                .map(ChronoZonedDateTime::toInstant)
                .map(SunPosition::newSunPosition)
                .forEach(System.out::println);
        //График
        Map<Number, Number> map = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            map.put(startTime.plusHours(i).getHour(),
                    newSunPosition(startTime.plusHours(i).toInstant()).elevation);
        }
        LineChartNumber.setChartTitle("Sun position");
        LineChartNumber.setSeriesName("Altitude");
        LineChartNumber.setXAxisLabel("Hour of day");
        LineChartNumber.goWith(map);
    }

    private static SunPosition newSunPosition(Instant instant) {
        return new SunPosition(
                new Position(60, 30, 20),
                instant.plus(1, ChronoUnit.HOURS));
    }
}