package edu.spbpu.logic.services;

import edu.spbpu.dao.SolarModuleDAO;
import edu.spbpu.models.SolarModule;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * Created by Vasiliy Bobkov on 07.04.2017.
 */
@RequiredArgsConstructor
public class SolarModuleService {
    private final SolarModuleDAO solarModuleDAO;

    public Optional<SolarModule> getById(Long id) {
        return solarModuleDAO.read(id);
    }

    public List<SolarModule> getAll() {
        return solarModuleDAO.getAll();
    }
}
