package edu.spbpu.dao;

import edu.spbpu.models.LocalData;
import edu.spbpu.models.Position;

public interface LocalDataDAO extends BaseDAO<LocalData, Long> {
    enum LocalDataType{
        WIND_2M,
        GLOBAL_RADIATION,
        CLEAR_SKY_RADIATION,
        TEMPERATURE;
    }
    LocalData getByCoordinate(Position position, LocalDataType type);
}
