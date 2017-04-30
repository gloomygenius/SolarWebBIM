package edu.spbpu.dao.fileImpl;

import edu.spbpu.dao.InverterDAO;
import edu.spbpu.models.Inverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Vasiliy Bobkov on 30.03.2017.
 */
@RequiredArgsConstructor
public class InverterDAOimpl implements InverterDAO {
    private final String filePath;
    private final List<Inverter> inverterList;

    @SneakyThrows
    public InverterDAOimpl(String path) {
        filePath = path;
        List<Inverter> inverters;
        try (Reader reader = new FileReader(filePath)) {
            CSVParser parser = CSVFormat.DEFAULT.withDelimiter(';').parse(reader);
            List<CSVRecord> records = parser.getRecords();
            inverters = records.stream()
                    .skip(3)
                    .map(this::parseFromRecord)
                    .collect(Collectors.toList());
        }
        inverterList = inverters;
    }
    private Inverter parseFromRecord(CSVRecord record) {
        return Inverter.builder()
                .id(Long.parseLong(record.get(0)))
                .name(record.get(1))
                .V_ac0(Double.valueOf(record.get(2)))
                .P_ac0(Double.valueOf(record.get(3)))
                .P_dc0(Double.valueOf(record.get(4)))
                .V_dc0(Double.valueOf(record.get(5)))
                .P_s0(Double.valueOf(record.get(6)))
                .C0(Double.valueOf(record.get(7)))
                .C1(Double.valueOf(record.get(8)))
                .C2(Double.valueOf(record.get(9)))
                .C3(Double.valueOf(record.get(10)))
                .P_nt(Double.valueOf(record.get(11)))
                .V_dcmax(Double.valueOf(record.get(12)))
                .I_dcmax(Double.valueOf(record.get(13)))
                .Mppt_low(Double.valueOf(record.get(14)))
                .Mppt_high(Double.valueOf(record.get(15)))
                .connections(Integer.parseInt(record.get(16)))
                .build();
    }

    @Override
    @SneakyThrows
    public List<Inverter> getAll() {
        return inverterList;
    }

    @Override
    public Optional<Inverter> getByName(String name) {
        return inverterList.stream().filter(s->s.getName().equals(name)).findFirst();
    }

    @Override
    public void create(Inverter entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Inverter> read(Long key) {
        return inverterList.stream().filter(s-> Objects.equals(s.getId(), key)).findFirst();
    }

    @Override
    public void update(Inverter entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException();
    }
}
