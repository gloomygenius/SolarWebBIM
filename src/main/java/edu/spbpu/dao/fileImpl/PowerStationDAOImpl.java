package edu.spbpu.dao.fileImpl;

import edu.spbpu.dao.PowerStationDAO;
import edu.spbpu.models.PowerStation;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Created by Vasiliy Bobkov on 30.03.2017.
 */
public class PowerStationDAOImpl implements PowerStationDAO {
    private AtomicLong currentId = new AtomicLong(0);
    private Map<Long, PowerStation> map = new HashMap<>();

    @Override
    public void create(PowerStation powerStation) {
        long id = currentId.incrementAndGet();
        powerStation.setId(id);
        map.put(id, powerStation);
    }

    @Override
    public Optional<PowerStation> read(Long key) {
        return Optional.ofNullable(map.get(key));
    }

    @Override
    public void update(PowerStation powerStation) {
        map.replace(powerStation.getId(), powerStation);
    }

    @Override
    public void delete(Long id) {
        map.remove(id);
    }

    @Override
    public List<PowerStation> getAll() {
        return map.entrySet()
                .stream()
                .sorted(Comparator.comparingLong(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}