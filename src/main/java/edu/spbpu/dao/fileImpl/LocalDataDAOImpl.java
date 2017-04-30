package edu.spbpu.dao.fileImpl;

import edu.spbpu.dao.LocalDataDAO;
import edu.spbpu.models.LocalData;
import edu.spbpu.models.Position;
import edu.spbpu.models.SunPosition;
import edu.spbpu.util.FileManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Vasiliy Bobkov on 28.03.2017.
 */
@AllArgsConstructor
public class LocalDataDAOImpl implements LocalDataDAO {
    private final String path;

    @Override
    public void create(LocalData entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<LocalData> read(Long key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(LocalData entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    @Override
    public LocalData getByCoordinate(Position position, LocalDataType type) {
        if (position == null) throw new RuntimeException("Coordinate is not initialized");
        //проверяем, есть ли ресурсы в кэше
        Optional<LocalData> localDataOptional = loadCachedResource(path + "/local/", position, type);
        if (localDataOptional.isPresent()) return localDataOptional.get();
        String typeDir;
        switch (type) {
            case GLOBAL_RADIATION:
                typeDir = "global_radiation";
                break;
            case CLEAR_SKY_RADIATION:
                typeDir = "clear_sky_radiation";
                break;
            case TEMPERATURE:
                typeDir = "temperature";
                break;
            case WIND_2M:
                typeDir = "wind2M";
                break;
            default:
                typeDir = "";
        }
        String cashPath = path + "/local/" + position.getLatitude() + "_" + position.getLongitude() + "\\" + typeDir + ".gz";
        String path = this.path + "/" + typeDir + "/";
        LocalData localData =
                Files.list(Paths.get(path))
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .peek(System.out::println)
                        .map(GlobalData::readFromOldFormat)
                        .map(globalData -> globalData.toLocalData(position))
                        .reduce(LocalData::merge)
                        .orElseThrow(() -> new RuntimeException("LocalData doesn't exist"));
        //кэшируем ресурсы
        saveLocalResource(cashPath, localData);
        return localData;
    }

    @SuppressWarnings("Duplicates")
    private Optional<LocalData> loadCachedResource(String path, Position coordinate, LocalDataType type) {
        String typeDir = "";

        switch (type) {
            case GLOBAL_RADIATION:
                typeDir = "global_radiation";
                break;
            case CLEAR_SKY_RADIATION:
                typeDir = "clear_sky_radiation";
                break;
            case TEMPERATURE:
                typeDir = "temperature";
                break;
            case WIND_2M:
                typeDir = "wind2m";
                break;
        }
        File directory = new File(path + coordinate.getLatitude() + "_" + coordinate.getLongitude() + "\\" + typeDir + ".gz");
        if (!directory.exists()) return Optional.empty();
        LocalData localData = FileManager.readFromGZ(directory.getAbsolutePath());
        return Optional.of(localData);
    }

    private void saveLocalResource(String path, LocalData localData) {
        File directory = new File(path);
        if (!directory.getParentFile().exists() && !directory.getParentFile().mkdir())
            throw new RuntimeException("directory " + directory.getAbsolutePath() + " is not made");
        FileManager.saveToGZ(path, localData);
    }

    @Data
    public static class GlobalData implements Serializable {
        private static final long serialVersionUID = 862841359675998516L;
        /**
         * Название параметра, информация о котором хранится в массиве данных
         */
        private String parameter;
        /**
         * Описание данного параметра
         */
        private String description;
        /**
         * Отображение данных на время. Каждому часу по UTC соответствует двумерный массив, в котором хранитятся значения
         * для всех точек сети реанализа
         */
        private Map<Instant, float[][]> data;

        public GlobalData(Map<Instant, float[][]> map) {
            data = map;
        }

        public GlobalData(Map<Instant, float[][]> map, String parameter) {
            data = map;
            this.parameter = parameter;
        }

        /**
         * Метод вычленения данных для определённой точки на сетке
         *
         * @param position координата точки
         * @return локальный массив данных LocalData
         */
        public LocalData toLocalData(Position position) {
            int latIndex = position.getLatitudeIndex();
            int longIndex = position.getLongitudeIndex();
            Map<Instant, Float> map = data.keySet().stream()
                    .collect(Collectors.toMap(
                            instant -> instant,
                            instant -> data.get(instant)[latIndex][longIndex]));
            return new LocalData(parameter, position, map);
        }

        /**
         * Метод сохранения массива данных в файл в формате gz. Внимание! Сохраняется не данный класс, а только поле data!
         *
         * @param path
         */
        @SuppressWarnings("Duplicates")
        public void saveToFile(String path) {
            //if (!new File(path).isDirectory()) throw new RuntimeException("Path " + path + " is not a directory");
            String name = path + "\\" + createFileName();
            File file = new File(name);
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdir()) throw new RuntimeException("Path " + name + " can't be create");
            }
            name = name.replaceAll("\\\\\\\\", "\\\\"); //меняем два слэша на один, если вдруг так получилось
            try (FileOutputStream stream = new FileOutputStream(name);
                 GZIPOutputStream gz = new GZIPOutputStream(stream);
                 ObjectOutputStream out = new ObjectOutputStream(gz)) {
                out.writeObject(data);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Вспомогательный метод для создания унифицированного названия файла
         *
         * @return название файла
         */
        private String createFileName() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("UTC"));
            String maxDate = data.keySet()
                    .stream()
                    .max(Instant::compareTo)
                    .map(formatter::format)
                    .orElse("");
            String minDate = data.keySet()
                    .stream()
                    .min(Instant::compareTo)
                    .map(formatter::format)
                    .orElse("");
            return parameter + "from" + minDate + "to" + maxDate + ".gz";
        }

        /**
         * Метод читает данные из файла формата gz, в котором сохранены обработанные данные
         *
         * @param filePath путь к файлу
         * @return массив данных по всему миру для указанного в файле периода
         */
        public static GlobalData readFromOldFormat(String filePath) {
            Map<Instant, float[][]> map = null;
            try (FileInputStream stream = new FileInputStream(filePath);
                 GZIPInputStream gz = new GZIPInputStream(stream);
                 ObjectInputStream out = new ObjectInputStream(gz)) {
                map = (Map<Instant, float[][]>) out.readObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return new GlobalData(map);
        }

        @SneakyThrows
        public static void main(String[] args) {
            //        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("UTC"));
            //        LocalDate startDate = LocalDate.of(2016, 1, 1);
            //        LocalDate endDate;
            //        for (int i = 0; i < 12; i++) {
            //            endDate = startDate.plus(i, ChronoUnit.MONTHS).with(TemporalAdjusters.lastDayOfMonth());
            //            String name1 = "E:\\MERRA 2\\result\\wind\\V2Mfrom"
            //                    + formatter.format(startDate.plus(i, ChronoUnit.MONTHS))
            //                    + "to"
            //                    + formatter.format(endDate)
            //                    + ".gz";
            //            String name2 = "E:\\MERRA 2\\result\\wind\\U2Mfrom"
            //                    + formatter.format(startDate.plus(i, ChronoUnit.MONTHS))
            //                    + "to"
            //                    + formatter.format(endDate)
            //                    + ".gz";
            //            GlobalData globalData1 = readFromOldFormat(name1);
            //            GlobalData globalData2 = readFromOldFormat(name2);
            //            globalData1.rectangleDataToRadius(globalData2);
            //            globalData1.setParameter("WIND");
            //            globalData1.saveToFile("E:\\MERRA 2\\result\\wind\\new\\");
            //        }

            //        readFromOldFormat("E:\\MERRA 2\\result\\solar\\SWGDNfrom2016-06-01to2016-06-30.gz")
            //                .toLocalData(new Position(60, 30, 20))
            //                .print();

            Thread thread = new Thread();
            Class<SunPosition> stringClass = SunPosition.class;
            Stream.of(stringClass.getDeclaredMethods())
                    .map(Method::getName)
                    .forEach(System.out::println);
            //        System.out.println(stringClass.getDeclaredMethods()[1].getName());
        }

        /**
         * Метод предназначен для преобразования двух массивов данных о скоростях ветра (северная и восточная составляющие)
         * в единое значение скорости ветра. При этом теряется информация о направлении ветра.
         * V=sqrt(v*v+u*u);
         *
         * @param globalData массив данных о второй составляющей ветра
         */
        public void rectangleDataToRadius(GlobalData globalData) {
            for (Instant instant : data.keySet()) {
                float[][] array1 = data.get(instant);
                float[][] array2 = globalData
                        .getData()
                        .get(instant);
                for (int i = 0; i < 361; i++) {
                    for (int j = 0; j < 576; j++) {
                        double a = array1[i][j];
                        double b = array2[i][j];
                        array1[i][j] = (float) Math.sqrt(a * a + b * b);
                    }
                }
                data.put(instant, array1);
            }
        }

        public static GlobalData rectangleDataToAzimuth(GlobalData eastWardData, GlobalData northWardData) {
            GlobalData linkedData = GlobalData.empty();
            for (Instant instant : eastWardData.data.keySet()) {
                float[][] eastWardArray = eastWardData.data.get(instant);
                float[][] northWardArray = northWardData.data.get(instant);
                float[][] newArray = new float[361][576];
                for (int i = 0; i < 361; i++) {
                    for (int j = 0; j < 576; j++) {
                        double a = eastWardArray[i][j];
                        double b = northWardArray[i][j];
                        newArray[i][j] = (float) (Math.atan2(a, -b) * 180 / Math.PI + 180);
                    }
                }
                linkedData.data.put(instant, newArray);
            }
            return linkedData;
        }

        private static GlobalData empty() {
            return new GlobalData(new HashMap<>());
        }
    }
}
