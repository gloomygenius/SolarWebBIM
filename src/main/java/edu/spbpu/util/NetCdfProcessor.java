package edu.spbpu.util;

import edu.spbpu.dao.fileImpl.LocalDataDAOImpl;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import edu.spbpu.models.Position;
import ucar.ma2.Array;
import ucar.ma2.Index;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Log4j
public class NetCdfProcessor {
    public static Map<Instant, float[][]> readFromNc4(String filename, String variableName) {
        Map<Instant, float[][]> dataMap;
        try (NetcdfFile ncfile = NetcdfFile.open(filename)) {
            String beginingDate = ncfile.findGlobalAttribute("RangeBeginningDate").getStringValue();
            Instant startTime = LocalDate
                    .parse(beginingDate)
                    .atTime(0, 0)
                    .toInstant(ZoneOffset.UTC);
            Instant[] time = new Instant[24];
            for (int i = 0; i < 24; i++) time[i] = startTime.plus(i, ChronoUnit.HOURS);
            Variable variable = ncfile.findVariable(variableName);
            log.debug("start data reading");
            Array data = variable.read();

            dataMap = IntStream
                    .range(0, 24)
                    .boxed()
                    .collect(Collectors.toMap(
                            t -> time[t],
                            t -> readSeries(t, data)));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dataMap;
    }

    private static float[][] readSeries(int timeIndex, Array data) {

        Index index = data.getIndex();
        float[][] value = new float[361][576];

        for (double latitude = -90; latitude <= 90; latitude += 0.5) {
            for (double longitude = -180; longitude <= 179.375; longitude += 0.625) {
                int latIndex = Position.transformLatitude(latitude);
                int lonIndex = Position.transformLongitude(longitude);
                index = index.set(
                        timeIndex,
                        latIndex,
                        lonIndex);
                value[latIndex][lonIndex] = (float) data.getDouble(index);
            }
        }
        return value;
    }

    @SneakyThrows
    public static Map<Instant, float[][]> readAllNC4(String path, String variable) {
        return Files.list(Paths.get(path))
                .collect(Collectors.toList())
                .parallelStream()
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .peek(System.out::println)
                .map(s -> readFromNc4(s, variable))
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue));
    }

    public static void save(String path, String variable, Map<Instant, float[][]> map) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("UTC"));
        String maxDate = map.keySet()
                .stream()
                .max(Instant::compareTo)
                .map(formatter::format)
                .orElse("");
        String minDate = map.keySet()
                .stream()
                .min(Instant::compareTo)
                .map(formatter::format)
                .orElse("");
        String name = path + "\\result\\" + variable + "from" + minDate + "to" + maxDate + ".gz";
        File file = new File(name);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdir()) throw new RuntimeException("Path can't be create");
        }
        name = name.replaceAll("\\\\\\\\", "\\\\"); //меняем два слэша на один, если вдруг так получилось
        FileManager.saveToGZ(name, map);
//        try (FileOutputStream stream = new FileOutputStream(name);
//             GZIPOutputStream gz = new GZIPOutputStream(stream);
//             ObjectOutputStream out = new ObjectOutputStream(gz)) {
//            out.writeObject(map);
//            out.flush();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static void transformAndSave(String path, String variable) {
        Map<Instant, float[][]> map = readAllNC4(path, variable);
        save("E:\\MERRA 2\\", variable, map);
    }

    private static LocalDataDAOImpl.GlobalData readFromFile(String filePath) {
        Map<Instant, float[][]> map = FileManager.readFromGZ(filePath);
        return new LocalDataDAOImpl.GlobalData(map);
    }

    public static void main(String[] args) {
//        transformAndSave("E:\\MERRA 2\\", "ALBEDO");
//
//        GlobalData globalDataNorth = new GlobalData(readAllNC4("E:\\MERRA 2\\", "V10M"));
//        GlobalData globalDataEast = new GlobalData(readAllNC4("E:\\MERRA 2\\", "U10M"));
//        GlobalData azimuthData = GlobalData.rectangleDataToAzimuth(globalDataEast, globalDataNorth);
//        update("E:\\MERRA 2\\", "AZ", azimuthData.getData());
//
//        globalDataEast.rectangleDataToRadius(globalDataNorth);
//        update("E:\\MERRA 2\\", "WIND10M", globalDataEast.getData());

//        transformAndSave("E:\\MERRA 2\\", "ALBEDO");
//        transformAndSave("E:\\MERRA 2\\", "SWGDN");
//        transformAndSave("E:\\MERRA 2\\", "SWGDNCLR");

//        LocalDate startDay = LocalDate.of(2016, 1, 1);
//        printListOfLinks(startDay, 3);
    }

    private static void printListOfLinks(LocalDate startDate, int month) {
        Stream.iterate(startDate, s -> s.plusDays(1))
                .limit(357)
                .filter(ld -> ld.getYear() == startDate.getYear())
                .filter(ld -> ld.getMonthValue() <= (startDate.getMonthValue() + month - 1))
                .map(NetCdfProcessor::generateLink)
                .forEach(System.out::println);
    }

    private static String generateLink(LocalDate date) {
        StringBuilder builder = new StringBuilder("https://goldsmr4.gesdisc.eosdis.nasa.gov/data/MERRA2/M2T1NXRAD.5.12.4/");
//        StringBuilder builder = new StringBuilder("https://goldsmr4.gesdisc.eosdis.nasa.gov/data/MERRA2/M2T1NXSLV.5.12.4/");
        builder.append(date.getYear())
                .append("/");
        if (date.getMonthValue() < 10) builder.append("0");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        builder.append(date.getMonthValue())
                .append("/MERRA2_400.tavg1_2d_rad_Nx.")
//                .append("/MERRA2_400.tavg1_2d_slv_Nx.")
                .append(formatter.format(date))
                .append(".nc4");
        return builder.toString();
    }
}