package edu.spbpu.dao;

import edu.spbpu.models.Inverter;

import java.util.List;
import java.util.Optional;

/**
 * Created by Vasiliy Bobkov on 30.03.2017.
 */
public interface InverterDAO extends BaseDAO<Inverter,Long> {
    List<Inverter> getAll();

    Optional<Inverter> getByName(String name);
}
