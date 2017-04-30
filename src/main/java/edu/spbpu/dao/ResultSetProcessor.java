package edu.spbpu.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetProcessor<E> {
     E execute(ResultSet resultSet)
            throws SQLException;
}