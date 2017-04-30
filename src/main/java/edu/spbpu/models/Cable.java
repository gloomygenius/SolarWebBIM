package edu.spbpu.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

@Value
public class Cable {
    private static final double MAX_LOSS = 0.025; //максимальные потери в кабеле
    private final double resistivity; //удельное сопротивление Ом*мм2/м
    private final double length;
    private final double crossSection;
    private final double resistanse;
    @Getter
    private static final double[] sections = {1.5, 2.5, 4, 6, 10, 16, 25, 35, 50, 70, 95, 120, 240};

    @AllArgsConstructor
    public enum Material {
        CUPRUM(10, 0.018),
        ALUMINIUM(8, 0.0295);
        @Getter
        private final double maxPassportCurrent;
        @Getter
        private final double resistivity;
    }

    public Cable(double length, double maxVoltage, double maxCurrent, Material material) {
        this.length = length;
        this.resistivity = material.resistivity;//удельное сопротивление меди

        crossSection = DoubleStream.of(sections)
                .filter(crossSection -> crossSection * material.getMaxPassportCurrent() > maxCurrent)
                .filter(crossSection -> (resistivity * length / crossSection) * maxCurrent / maxVoltage < MAX_LOSS)
                .min()
                .orElseThrow(() -> new RuntimeException("Cable has cross section more than 120 mm2"));
        resistanse = resistivity * length / crossSection;
    }

    public Cable(double length, double voltage, double maxCurrent) {
        this(length, voltage, maxCurrent, Material.CUPRUM); //медный кабель в качестве стандартного
    }

    public double getVoltageWithLoss(double voltage, double current) {
        double voltageLoss = resistanse * current;
        return voltage - voltageLoss;
    }

    public static void main(String[] args) {
        double length = 65;
        Map<Double, Integer> map = Stream.iterate(1, s -> s + 1)
                .limit(8)
                .flatMap(s -> Stream.of(length * s, length * (s + 1), length * (s + 2), length * (s + 3), length * (s + 4)))
                .collect(Collectors.toMap(
                        Function.identity(),
                        s->5,
                        (g, y) -> g+5));
        map.entrySet()
                .stream()
                .sorted(Comparator.comparingDouble(Map.Entry::getKey))
                .forEach(s -> System.out.println(s.getKey() + " " + s.getValue()));
    }
}