package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;

public class QueryFetcher {

  private static final Logger LOG = LoggerFactory.getLogger(QueryFetcher.class);

  public static Records getQueryResults(PreparedStatement preparedStatement, Set<Column> selectedColumns, BaseDatabaseConnection dbConnection) throws SQLException {
    ResultSet resultSet = null;

    try {
      resultSet = preparedStatement.executeQuery();
      Records results = new Records();
      while (resultSet.next()) {
        Record record = parseResultSet(resultSet, selectedColumns);
        if (record != null) {
          results.addRecord(record);
        }
      }
      return results;
    } catch (SQLRecoverableException e) {
      dbConnection.resetConnection();
      throw e;
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
        preparedStatement.close();
      } catch (SQLRecoverableException e) {
        LOG.error(e.toString());
        dbConnection.resetConnection();
      } catch (SQLException e) {
        LOG.error(e.toString());
      }
    }
  }

  @FunctionalInterface
  private interface ItemGetter {
    Object getItem(ResultSet resultSet, String sqlKeyword) throws SQLException;
  }

  private static Map<Class<?>, ItemGetter> itemGetters = ImmutableMap.<Class<?>, ItemGetter>builder()
      .put(Integer.class, ResultSet::getInt)
      .put(Long.class, ResultSet::getLong)
      .put(java.sql.Date.class, QueryFetcher::getDate)
      .put(Timestamp.class, QueryFetcher::getTimestamp)
      .put(Double.class, ResultSet::getDouble)
      .put(String.class, ResultSet::getString)
      .put(Boolean.class, ResultSet::getBoolean)
      .put(byte[].class, ResultSet::getBytes)
      .build();

  private static Long getDate(ResultSet resultSet, String sqlKeyword) throws SQLException {
    java.sql.Date date = resultSet.getDate(sqlKeyword);
    return date == null ? null : date.getTime();
  }

  private static Long getTimestamp(ResultSet resultSet, String sqlKeyword) throws SQLException {
    Timestamp timestamp = resultSet.getTimestamp(sqlKeyword);
    return timestamp == null ? null : timestamp.getTime();
  }

  private static Record parseResultSet(ResultSet resultSet, Set<Column> selectedColumns) throws SQLException {
    if (selectedColumns.isEmpty()) {
      return null;
    }

    Record record = new Record(selectedColumns.size());
    for (Column column : selectedColumns) {
      String sqlKeyword = column.getSqlKeyword();
      Class type = column.getType();
      Object value;
      ItemGetter itemGetter = itemGetters.containsKey(type) ? itemGetters.get(type) : ResultSet::getObject;
      value = itemGetter.getItem(resultSet, sqlKeyword);

      if (resultSet.wasNull()) {
        value = null;
      }

      record.addColumn(column, value);
    }
    return record;
  }
}
