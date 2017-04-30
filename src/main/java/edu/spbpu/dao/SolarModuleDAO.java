package edu.spbpu.dao;

import edu.spbpu.models.SolarModule;

import java.util.List;

/**
 * Created by Vasiliy Bobkov on 29.03.2017.
 */
public interface SolarModuleDAO extends BaseDAO<SolarModule, Long> {
    List<SolarModule> getAll();
}