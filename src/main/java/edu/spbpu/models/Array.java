package edu.spbpu.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Vasiliy Bobkov on 31.03.2017.
 */
@Data
@Builder
@AllArgsConstructor
public class Array implements Serializable {
    private static final long serialVersionUID = 5895189380514953052L;
    private Battery battery;
    private int amountString;
    @Deprecated
    private double cableCrossSection;
    private List<Cable> cables;

    public double getP_max(SolarModule.Operation operation) {
        double moduleStringPower = battery.getP_max(operation);
        double voltage = moduleStringPower / operation.getMpCurrent();
        double current = operation.getMpCurrent();
        double circuitVoltage = cables.stream()
                .mapToDouble(cable -> cable.getVoltageWithLoss(voltage, current))
                .summaryStatistics()
                .getAverage();
        double isStringCurrent = amountString * current;
        return circuitVoltage * isStringCurrent;
    }

    public static void main(String[] args) {

    }
}