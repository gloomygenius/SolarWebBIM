package edu.spbpu.dao;

import edu.spbpu.models.Inverter;
import edu.spbpu.models.PowerStation;

/**
 * Created by Vasiliy Bobkov on 29.03.2017.
 */
public interface DAOAbstractFactory {
    LocalDataDAO createLocalDataDAO();
    SolarModuleDAO createSolarModuleDAO();
    PowerStationDAO createPowerStationDAO();
    InverterDAO createInverterDAO();
}
