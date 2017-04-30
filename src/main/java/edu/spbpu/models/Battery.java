package edu.spbpu.models;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Vasiliy Bobkov on 28.03.2017.
 */
@Builder
@Data
public class Battery {
    private final long powerStationId;

    public double getP_max(SolarModule.Operation operation) {
        return amountOfPaneles * operation.getMaxPower();
    }

    public enum Orientation {
        VERTICAL,
        HORIZONTAL
    }

    @Builder.Default
    private Orientation orientation = Orientation.VERTICAL;
    private SolarModule module; //Тип модуля
    private int amountOfPaneles; //количество модулей в цепи
}