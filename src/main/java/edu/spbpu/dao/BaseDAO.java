package edu.spbpu.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface BaseDAO<E, K> {
    void create(E entity);

    Optional<E> read(K key);

    void update(E entity);

    void delete(K id);

    default <T> List<T> executeQuery(Connection connection,
                                     String sql,
                                     ResultSetProcessor<T> processor,
                                     Object... params) {
        List<T> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int cnt = 0;
            for (Object param : params) {
                ps.setObject(++cnt, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(processor.execute(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
        return list;
    }
    default void executeUpdate(Connection connection,
                               String sql,
                               Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int cnt = 0;
            for (Object param : params) {
                ps.setObject(++cnt, param);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}