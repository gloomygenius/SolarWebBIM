package edu.spbpu.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log4j
public class PowerStation implements Serializable {
    private static final long serialVersionUID = -9166695744756224585L;
    private long id;
    private String name;
    private Position position;
    private SolarModule solarModule;
    private LocalData windSpeedData;
    private LocalData directRadiation;
    private LocalData diffuseRadiation;
    private LocalData ambientTemperature;
    @Builder.Default
    private ZoneId timeZone = ZoneId.of("UTC");
    private Array string;
    @Deprecated
    private int stringAmount;
    private Inverter inverter;
    private int inverterAmount;
    private Double nominalPower; //в ваттах
    @Deprecated
    private Double actualPower;
    private Double betta;
    private Integer rowsOfPanel;
    private Map<ZonedDateTime, SolarModule.Operation> cachedOperations; //Кэш режимов работы панели
    private SolarModule.Operation maxOperation;
    private List<Cable> cablesFromStringToInverter; //список кабелей от одного инвертора до сборок панелей

    @Data
    @Builder
    @Log4j
    public static class Operation {

        private double annualGeneration;    //Годовая выработка
        private double inverterLoad;    //Загрузка инвертора
        private double utilizationFactor; //KIUM
        private double ocVoltage;
        private double nominalVoltage;
    }

    public double getPowerFromOneInverter(SolarModule.Operation operation, boolean overLoad) {
        if (operation == SolarModule.Operation.NIGHT || operation.getMaxPower() == 0)
            return -inverter.getP_nt();
        double isStringPower = string.getP_max(operation);
        double allStringPower = isStringPower * inverter.getConnections();
        if (allStringPower < inverter.getP_s0()) return allStringPower - inverter.getP_s0();
        double isStringCurrent = string.getAmountString() * operation.getMpCurrent();
        double voltage = cablesFromStringToInverter.stream()
                .mapToDouble(s -> s.getVoltageWithLoss(isStringPower / isStringCurrent, isStringCurrent))
                .summaryStatistics()
                .getAverage();
        double power = inverter.calculateACPower(isStringPower * inverter.getConnections(), voltage);
        if (!overLoad && power > inverter.getP_ac0()) power = inverter.getP_ac0();
        return power;
    }

    public double getCurrentPower(SolarModule.Operation operation) {
        return getPowerFromOneInverter(operation, false) * inverterAmount;
    }

    public Map<ZonedDateTime, SolarModule.Operation> calculateOneYear(double betta) {
        return Stream.
                iterate(ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, timeZone),
                        zdt -> zdt.plusHours(1))
                .limit(8784)
                .parallel()
                .collect(Collectors.toMap(
                        s -> s,
                        dt -> solarModule.getOperation(
                                ambientTemperature.getValue(dt),
                                directRadiation.getValue(dt),
                                diffuseRadiation.getValue(dt),
                                windSpeedData.getValue(dt),
                                new SunPosition(position, dt.toInstant()),
                                betta)));
    }

    @SuppressWarnings("ConstantConditions")
    public void calculateMaxOperation() {
        if (Objects.isNull(cachedOperations)) cachedOperations = calculateOneYear(betta);
        double V_max = cachedOperations
                .values()
                .stream()
                .mapToDouble(SolarModule.Operation::getOcVoltage)
                .max()
                .getAsDouble();
        double I_max = cachedOperations
                .values()
                .stream()
                .mapToDouble(SolarModule.Operation::getScCurrent)
                .max()
                .getAsDouble();
        double P_max = cachedOperations
                .values()
                .stream()
                .mapToDouble(SolarModule.Operation::getMaxPower)
                .max()
                .getAsDouble();
        double V_mpmax = cachedOperations
                .values()
                .stream()
                .mapToDouble(SolarModule.Operation::getMpVolatage)
                .max()
                .getAsDouble();
        double I_mpmax = cachedOperations
                .values()
                .stream()
                .mapToDouble(SolarModule.Operation::getMpCurrent)
                .max()
                .getAsDouble();
        this.maxOperation = SolarModule.Operation
                .builder()
                .maxPower(P_max)
                .mpCurrent(I_mpmax)
                .mpVolatage(V_mpmax)
                .ocVoltage(V_max)
                .scCurrent(I_max)
                .build();
    }
}