package edu.spbpu.logic.services;

import edu.spbpu.dao.BaseDAO;
import edu.spbpu.dao.LocalDataDAO;
import edu.spbpu.dao.PowerStationDAO;
import edu.spbpu.models.*;
import lombok.extern.log4j.Log4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.spbpu.Constants.LOCAL_DATA_DAO;
import static edu.spbpu.Constants.POWER_STATION_DAO;

@Log4j
public class PowerStationService {
    private final PowerStationDAO powerStationDAO;
    private final LocalDataDAO localDataDAO;

    public PowerStationService(Map<String, BaseDAO> daoMap) {
        powerStationDAO = (PowerStationDAO) daoMap.get(POWER_STATION_DAO);
        localDataDAO = (LocalDataDAO) daoMap.get(LOCAL_DATA_DAO);
    }

    public PowerStation optimizeBettaAndSave(PowerStation powerStation) {
        Map<Double, Double> operations = new HashMap<>();
        for (double betta = 5; betta < 90; betta++) {
            double annualGeneration = powerStation.calculateOneYear(betta)
                    .values()
                    .stream()
                    .mapToDouble(SolarModule.Operation::getMaxPower)
                    .sum();
//            System.out.println("angle: " + betta + " power:" + annualGeneration);
            operations.put(betta, annualGeneration);
        }
        powerStation.setBetta(operations.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElseGet(() -> powerStation.getPosition().getLatitude()));
        return powerStation;
    }

    public void createStation(Map<String, String> parameters) {
        String name = parameters.get("name");
        double latitude = Double.parseDouble(parameters.get("latitude"));
        double longitude = Double.parseDouble(parameters.get("longitude"));
        double altitude = Double.parseDouble(parameters.get("altitude"));
        double nominalPower = Double.parseDouble(parameters.get("nominal_power"));
        nominalPower *= 1000;
        int zoneId = Integer.parseInt(parameters.get("zone_id"));
        Position position = new Position(latitude, longitude, altitude);
        ZoneId zoneId1 = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(zoneId));

        LocalData ambientTemperatire = localDataDAO.getByCoordinate(position, LocalDataDAO.LocalDataType.TEMPERATURE);
        LocalData windSpeed = localDataDAO.getByCoordinate(position, LocalDataDAO.LocalDataType.WIND_2M);
        LocalData globalRadiationData = localDataDAO.getByCoordinate(position, LocalDataDAO.LocalDataType.GLOBAL_RADIATION);
        LocalData clearSkyRadiationData = localDataDAO.getByCoordinate(position, LocalDataDAO.LocalDataType.CLEAR_SKY_RADIATION);
        LocalData directRadiation = separateRadiationToDirect(globalRadiationData, clearSkyRadiationData);
        LocalData diffuseRadiation = separateRadiationToDiffuse(globalRadiationData, directRadiation);

        PowerStation station = PowerStation.builder()
                .name(name)
                .position(position)
                .timeZone(zoneId1)
                .nominalPower(nominalPower)
                .ambientTemperature(ambientTemperatire)
                .windSpeedData(windSpeed)
                .directRadiation(directRadiation)
                .diffuseRadiation(diffuseRadiation)
                .build();
        powerStationDAO.create(station);
    }

    private LocalData separateRadiationToDiffuse(LocalData globalRadiationData, LocalData directRadiation) {
        Map<Instant, Float> map = globalRadiationData.getData()
                .keySet()
                .parallelStream()
                .collect(
                        Collectors.toMap(s -> s, s -> globalRadiationData.getData().get(s) - directRadiation.getData().get(s)));
        return new LocalData("diffuse_radiation", map);
    }

    private LocalData separateRadiationToDirect(LocalData globalRadiation, LocalData clearSkyRadiation) {
        ToDoubleFunction<Instant> separator = (time) -> {
            double G = globalRadiation.getData().get(time);
            if (G == 0) return 0;
            double Gcs = clearSkyRadiation.getData().get(time);
            double kt = G / Gcs;
            double k;
            if (kt < 0.35) k = 1 - 0.249 * kt;
            else if (kt > 0.75) k = 0.177;
            else k = 1.577 - 1.84 * kt;
            return G * (1 - k);
        };
        Map<Instant, Float> map = globalRadiation.getData()
                .keySet()
                .stream()
                .collect(Collectors.toMap(Function.identity(),
                        s -> (float) separator.applyAsDouble(s)));
        return new LocalData("direct_radiation", map);
    }

    public List<PowerStation> getAll() {
        return powerStationDAO.getAll();
    }

    public Optional<PowerStation> getById(long id) {
        return powerStationDAO.read(id);
    }

    public PowerStation.PowerStationBuilder getBuilderById(long id) {
        PowerStation station = powerStationDAO.read(id).orElseThrow(RuntimeException::new);
        return PowerStation.builder()
                .ambientTemperature(station.getAmbientTemperature())
                .betta(station.getBetta())
                .cachedOperations(station.getCachedOperations())
                .directRadiation(station.getDirectRadiation())
                .windSpeedData(station.getWindSpeedData())
                .id(id)
                .inverter(station.getInverter())
                .solarModule(station.getSolarModule())
                .nominalPower(station.getNominalPower())
                .inverterAmount(station.getInverterAmount())
                .maxOperation(station.getMaxOperation())
                .rowsOfPanel(station.getRowsOfPanel())
                .stringAmount(station.getStringAmount())
                .string(station.getString())
                .position(station.getPosition())
                .timeZone(station.getTimeZone())
                .diffuseRadiation(station.getDiffuseRadiation())
                .name(station.getName());
    }

    public void update(PowerStation station) {
        powerStationDAO.update(station);
    }

    public void pickUpStings(PowerStation station) throws Exception {
        PowerStation.PowerStationBuilder builder = getBuilderById(station.getId());

        int amountOfModules = getMaxNumberOfModule(station);
        Battery battery = Battery.builder().amountOfPaneles(amountOfModules).build();
        int amountOfStrings = getMaxNumberOfString(station);
        if (amountOfStrings == 0) throw new Exception("Uncompatible module and inverter");
        Array array = Array.builder().battery(battery).amountString(amountOfStrings).build();
        builder.string(array);
        powerStationDAO.update(builder.build());
    }

    private int getMaxNumberOfString(PowerStation powerStation) {
        Inverter inverter = powerStation.getInverter();
        if (Objects.isNull(powerStation.getMaxOperation())) powerStation.calculateMaxOperation();
        return (int) Math.floor(inverter.getI_dcmax() / inverter.getConnections() / powerStation.getMaxOperation().getMpCurrent());
    }

    private int getMaxNumberOfModule(PowerStation powerStation) {
        if (Objects.isNull(powerStation.getMaxOperation())) powerStation.calculateMaxOperation();
        //Определяем максимально возможное напряжение на ModuleString
        SolarModule solarModule = powerStation.getSolarModule();
        Inverter inverter = powerStation.getInverter();
        double maxAllowableVoltage = Math.min(solarModule.getMaxCableVoltage(), inverter.getV_dcmax());
        log.debug("Max voltage on module: " + maxAllowableVoltage);
        getMaxNumberOfString(powerStation);
        //Считаем количество панелей в составе Module String с округлением в большую сторону
        return (int) Math.floor(maxAllowableVoltage / powerStation.getMaxOperation().getOcVoltage());
    }

    public void setCableLength(Map<String, String[]> parameterMap) {
        long id = Long.parseLong(parameterMap.get("id")[0]);
        PowerStation station = getById(id).orElseThrow(RuntimeException::new);

        String[] mStringCableLength = parameterMap.get("m_string_length[]");
        if (Objects.isNull(station.getMaxOperation())) station.calculateMaxOperation();
        @SuppressWarnings("ConstantConditions") SolarModule.Operation maxOperation = station
                .getCachedOperations()
                .entrySet()
                .parallelStream()
                .map(Map.Entry::getValue)
                .sorted(Comparator
                        .comparingDouble(SolarModule.Operation::getMpCurrent)
                        .reversed())
                .findFirst()
                .get();
        double maxStringVoltage = maxOperation.getMpVolatage() * station.getString().getBattery().getAmountOfPaneles();
        List<Cable> cables = Stream.of(mStringCableLength)
                .map(Double::valueOf)
                .map(length -> new Cable(length, maxStringVoltage, maxOperation.getMpCurrent()))
                .collect(Collectors.toList());
        station.getString().setCables(cables);

        String[] isStringCableLength = parameterMap.get("is_string_length[]");

        double maxIsCurrent = maxOperation.getScCurrent() * station.getString().getAmountString();
        List<Cable> isCables = Stream.of(isStringCableLength)
                .map(Double::valueOf)
                .map(length -> new Cable(length, maxStringVoltage, maxIsCurrent))
                .collect(Collectors.toList());
        station.setCablesFromStringToInverter(isCables);
    }

    public PowerStation.Operation getAnnualOperation(long stationId) {
        PowerStation station = getById(stationId).orElseThrow(RuntimeException::new);
        double maxInverterPowerOverloaded = getMaxInverterPower(station, true);
        log.debug("max inverter power overloaded: " + maxInverterPowerOverloaded);
        double maxInverterPower = getMaxInverterPower(station, false);
        findAndSetAmountOfInverter(station, maxInverterPower);
        double annualGeneration = station
                .getCachedOperations()
                .entrySet()
                .parallelStream()
                .map(Map.Entry::getValue)
                .mapToDouble(station::getCurrentPower)
                .sum();
        double utilizationFactor = annualGeneration / (station.getNominalPower() * 8760);
        double inverterLoad = maxInverterPowerOverloaded / station.getInverter().getP_ac0();
        double macOcVoltage = station.getCachedOperations()
                .entrySet()
                .parallelStream()
                .map(Map.Entry::getValue)
                .mapToDouble(SolarModule.Operation::getOcVoltage)
                .max()
                .getAsDouble();
        macOcVoltage = macOcVoltage * station.getString().getBattery().getAmountOfPaneles();
        return PowerStation.Operation.builder()
                .annualGeneration(annualGeneration)
                .inverterLoad(inverterLoad)
                .utilizationFactor(utilizationFactor)
                .ocVoltage(macOcVoltage)
                .nominalVoltage(0)
                .build();
    }

    private double getMaxInverterPower(PowerStation station, boolean overLoad) {
        @SuppressWarnings("ConstantConditions") double maxPower = station.getCachedOperations()
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .mapToDouble(operation -> station.getPowerFromOneInverter(operation, overLoad))
                .max()
                .getAsDouble();
        log.debug("max power of inverter: " + maxPower);
        return maxPower;
    }

    private void findAndSetAmountOfInverter(PowerStation station, double maxInverterPower) {
        int amount = (int) Math.ceil(station.getNominalPower() / maxInverterPower);
        log.debug("amountOfPaneles inverter: " + Math.ceil(station.getNominalPower() / maxInverterPower));
        station.setInverterAmount(amount);
    }

    public Map<String, Double> calculateCapex(Map<String, String[]> parameterMap) {
        if (!parameterMap.containsKey("orientation")) return new HashMap<>();
        double[] sections = Cable.getSections();
        Map<Double, Double> cablePrice = new HashMap<>();
        for (int i = 0; i < sections.length; i++) {
            Double price = Double.valueOf(parameterMap.get("cable_price")[i]);
            cablePrice.put(sections[i], price);
        }
        PowerStation station = getById(Long.parseLong(parameterMap.get("id")[0])).orElseThrow(RuntimeException::new);
        Inverter inverter = station.getInverter();
        //расчёт кабелей от инвертороы до сборкок панелей
        double cableCost = station.getCablesFromStringToInverter()
                .stream()
                .mapToDouble(cable -> {
                    double length = cable.getLength();
                    double price = cablePrice.get(cable.getCrossSection());
                    return length * price * station.getInverterAmount();
                })
                .sum();
        Battery.Orientation orientation = Battery.Orientation.valueOf(parameterMap.get("orientation")[0]);
        cableCost = cableCost + station.getString().getCables().stream()
                .mapToDouble(cable -> {
                    int moduleAmount = station.getString().getBattery().getAmountOfPaneles();
                    double moduleLength = station.getSolarModule().getHorizontalSize(orientation);
                    double length = cable.getLength() - moduleAmount * moduleLength;
                    double price = cablePrice.get(cable.getCrossSection());
                    return length * price * inverter.getConnections() * station.getInverterAmount();
                }).sum();
        Map<String, Double> capexMap = new HashMap<>();
        capexMap.put("cable_cost", cableCost);

        double inverterPrice = Double.parseDouble(parameterMap.get("inverter_price")[0]);
        double inverterCost = inverterPrice * station.getInverterAmount();
        capexMap.put("inverter_cost", inverterCost);

        double modulePrice = Double.parseDouble(parameterMap.get("module_price")[0]);
        double moduleAmount = station.getString().getBattery().getAmountOfPaneles() * station.getString().getAmountString() * station.getInverterAmount() * inverter.getConnections();
        double moduleCost = modulePrice * moduleAmount;
        capexMap.put("module_cost", moduleCost);

        return capexMap;
    }
}