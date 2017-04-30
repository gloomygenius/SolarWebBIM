package edu.spbpu.util;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import lombok.Setter;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;


public class LineChartNumber<X, Y> extends Application {
    private static Map elements;
    @Setter
    private static String seriesName = "Default series name";
    @Setter
    private static String chartTitle = "Default chart name";
    @Setter
    private static String title = "Chart";
    @Setter
    private static String xAxisLabel = "X axis label";
    @Setter
    private static String yAxisLabel = "Y axis label";

    @Override
    public void start(Stage stage) {
        stage.setTitle(title);
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xAxisLabel);
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        xAxis.autosize();
        yAxis.autosize();
        lineChart.setTitle(chartTitle);

        XYChart.Series series1 = new XYChart.Series<>();
        series1.setName(seriesName);
        ObservableList<XYChart.Data<X, Y>> dataList = series1.getData();
        for (Map.Entry<X, Y> entry : (Iterable<Map.Entry<X, Y>>) elements.entrySet()) {
            X key = entry.getKey();
            Y value = entry.getValue();
            dataList.add(new XYChart.Data<>(key, value));
        }
        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().addAll(series1);

        stage.setScene(scene);
        stage.show();
    }

    public static void goWith(Map dataMap) {
        LineChartNumber.elements = dataMap;
        launch();
    }

    public static void goWithInstant(Map<Instant, ? extends Number> map) {
        int i = 1;
        LineChartNumber.elements = map.entrySet()
                .stream()
                .sorted(Comparator.comparingLong(s -> s.getKey().getEpochSecond()))
                .collect(Collectors.toMap(s -> s.getKey().getEpochSecond() / 10E6, Map.Entry::getValue));
        launch();
    }
}