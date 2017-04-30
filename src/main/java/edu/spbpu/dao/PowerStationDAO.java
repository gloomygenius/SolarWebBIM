package edu.spbpu.dao;

import edu.spbpu.models.PowerStation;

import java.util.List;

/**
 * Created by Vasiliy Bobkov on 30.03.2017.
 */
public interface PowerStationDAO extends BaseDAO<PowerStation,Long> {
    List<PowerStation> getAll();
}
