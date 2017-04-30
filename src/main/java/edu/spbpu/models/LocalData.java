package edu.spbpu.models;

import edu.spbpu.util.LineChartNumber;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@Data
@AllArgsConstructor
public class LocalData implements Serializable {
    private static final long serialVersionUID = 9116546558503639478L;
    /**
     * Параметр массива данных (скорость ветра, температура и т.д.)
     */
    private String parameter;
    private Position position;
    /**
     * Отображение данных на время
     */
    private Map<Instant, Float> data;

    public LocalData(String parameter, Map<Instant, Float> data) {
        this.data = data;
        this.parameter = parameter;
    }

    /**
     * Метод объединяет два массива данных
     *
     * @param newData - присоединяемый массив локальных данных
     */
    public LocalData merge(LocalData newData) {
        data.putAll(newData.data);
        return this;
    }

//    /**
//     * Метод читает из всех файлов в указанной папке глобальные данные, потом преобразует к локальным данным
//     * и объединяет в один экземпляр
//     *  @param path     путь к папке с файлами данных (кроме файлов данных, не должно быть никаких других файлов)
//     * @param position координата для преобразования в локальные данные
//     */
//    @SneakyThrows
//    public static LocalData readFromFiles(String path, Position position) {
//        return Files.list(Paths.get(path))
//                .filter(Files::isRegularFile)
//                .map(Path::toString)
//                .peek(System.out::println)
//                .map(LocalDataDAOImpl.GlobalData::readFromOldFormat)
//                .map(globalData -> globalData.toLocalData(position))
//                .reduce(LocalData::merge)
//                .orElseThrow(()->new RuntimeException("LocalData doesn't exist"));
//    }

    public void printChart() {
        LineChartNumber.setChartTitle("Sun position");
        LineChartNumber.setSeriesName("Altitude");
        LineChartNumber.setXAxisLabel("Hour of day");
        LineChartNumber.goWithInstant(data);
    }

    public double getValue(ZonedDateTime dateTime) {
        dateTime = dateTime.withZoneSameInstant(ZoneId.of("UTC"))
                .withYear(2016);
        return data.get(dateTime.toInstant());
    }
}