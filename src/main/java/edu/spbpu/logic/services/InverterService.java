package edu.spbpu.logic.services;

import edu.spbpu.dao.InverterDAO;
import edu.spbpu.models.Inverter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * Created by Vasiliy Bobkov on 07.04.2017.
 */
@RequiredArgsConstructor
public class InverterService {
    private final InverterDAO inverterDAO;

    public Optional<Inverter> getById(long id){
        return inverterDAO.read(id);
    }

    public List<Inverter> getAll() {
        return inverterDAO.getAll();
    }
}