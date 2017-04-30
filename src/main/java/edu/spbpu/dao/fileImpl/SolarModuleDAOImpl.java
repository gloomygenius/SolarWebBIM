package edu.spbpu.dao.fileImpl;

import edu.spbpu.dao.SolarModuleDAO;
import edu.spbpu.models.SolarModule;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Vasiliy Bobkov on 29.03.2017.
 */
public class SolarModuleDAOImpl implements SolarModuleDAO {
    private String filePath;
    private final List<SolarModule> moduleList;

    @SneakyThrows
    public SolarModuleDAOImpl(String filePath) {
        List<SolarModule> solarModules;
        try (Reader reader = new FileReader(filePath);) {
            CSVParser parser = CSVFormat.DEFAULT.withDelimiter(';').parse(reader);
            List<CSVRecord> records = parser.getRecords();
            solarModules = records.stream()
                    .skip(3)
                    .map(this::parseFromRecord)
                    .collect(Collectors.toList());
        }
        moduleList = Collections.unmodifiableList(solarModules);
    }

    private SolarModule parseFromRecord(CSVRecord record) {
        return SolarModule.builder()
                .id(Long.parseLong(record.get(22)))
                .length(Double.valueOf(record.get(23)))
                .width(Double.valueOf(record.get(24)))
                .name(record.get(0))
                .Tnoct(Double.valueOf(record.get(3)))
                .area(Double.valueOf(record.get(4)))
                .Ns(Integer.valueOf(record.get(5)))
                .scCurrentRef(Double.valueOf(record.get(6)))
                .ocVoltageRef(Double.valueOf(record.get(7)))
                .I_mp_REF(Double.valueOf(record.get(8)))
                .V_mp_REF(Double.valueOf(record.get(9)))
                .alphaSC(Double.valueOf(record.get(10)))
                .bettaOC(Double.valueOf(record.get(11)))
                .a_REF(Double.valueOf(record.get(12)))
                .I_L_REF(Double.valueOf(record.get(13)))
                .I_0_REF(Double.valueOf(record.get(14)))
                .Rs_REF(Double.valueOf(record.get(15)))
                .Rsh_REF(Double.valueOf(record.get(16)))
                .build();
    }

    @Override
    @SneakyThrows
    public List<SolarModule> getAll() {
        return moduleList;
    }

    @Override
    public void create(SolarModule entity) {
        // TODO: 29.03.2017 реализовать
    }

    @Override
    public Optional<SolarModule> read(Long key) {
        return moduleList.stream().filter(s -> s.getId() == key).findFirst();
    }

    @Override
    public void update(SolarModule entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException();
    }
}
