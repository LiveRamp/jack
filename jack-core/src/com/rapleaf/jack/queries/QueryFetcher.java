package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import com.rapleaf.jack.BaseDatabaseConnection;

public class QueryFetcher extends BaseFetcher {

  public static Records getQueryResults(PreparedStatement preparedStatement, Collection<Column> selectedColumns, BaseDatabaseConnection dbConnection) throws SQLException {
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
      throw new RuntimeException(e);
    } finally {
      closeQuery(resultSet, preparedStatement, dbConnection);
    }
  }

  public static RecordIterator getQueryResultsStream(PreparedStatement preparedStatement, Set<Column> selectedColumns, BaseDatabaseConnection dbConnection) throws SQLException {
    preparedStatement.setFetchSize(Integer.MIN_VALUE);
    ResultSet results = preparedStatement.executeQuery();
    return new RecordIterator(preparedStatement, selectedColumns, results, dbConnection);
  }

  @FunctionalInterface
  private interface ItemGetter {
    Object getItem(ResultSet resultSet, String sqlKeyword) throws SQLException;
  }

  private static final Map<Class<?>, ItemGetter> ITEM_GETTERS = ImmutableMap.<Class<?>, ItemGetter>builder()
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

  public static Record parseResultSet(ResultSet resultSet, Collection<Column> selectedColumns) throws SQLException {
    if (selectedColumns.isEmpty()) {
      return null;
    }

    Record record = new Record(selectedColumns.size());
    for (Column column : selectedColumns) {
      String sqlKeyword = column.getSelectAlias();
      Class type = column.getType();
      Object value;
      ItemGetter itemGetter = ITEM_GETTERS.containsKey(type) ? ITEM_GETTERS.get(type) : ResultSet::getObject;
      value = itemGetter.getItem(resultSet, sqlKeyword);

      if (resultSet.wasNull()) {
        value = null;
      }

      record.addColumn(column, value);
    }
    return record;
  }
}
