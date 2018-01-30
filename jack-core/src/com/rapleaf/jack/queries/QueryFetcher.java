package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

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

  static Record parseResultSet(ResultSet resultSet, Collection<Column> selectedColumns) throws SQLException {
    if (selectedColumns.isEmpty()) {
      return null;
    }

    Record record = new Record(selectedColumns.size());
    for (Column column : selectedColumns) {
      String sqlKeyword = column.getSqlKeyword();
      Class type = column.getType();
      Object value;

      if (type == Integer.class) {
        value = resultSet.getInt(sqlKeyword);
      } else if (type == Long.class) {
        value = resultSet.getLong(sqlKeyword);
      } else if (type == java.sql.Date.class) {
        java.sql.Date date = resultSet.getDate(sqlKeyword);
        if (date != null) {
          value = date.getTime();
        } else {
          value = null;
        }
      } else if (type == Timestamp.class) {
        Timestamp timestamp = resultSet.getTimestamp(sqlKeyword);
        if (timestamp != null) {
          value = timestamp.getTime();
        } else {
          value = null;
        }
      } else if (type == Double.class) {
        value = resultSet.getDouble(sqlKeyword);
      } else if (type == String.class) {
        value = resultSet.getString(sqlKeyword);
      } else if (type == Boolean.class) {
        value = resultSet.getBoolean(sqlKeyword);
      } else if (type == byte[].class) {
        value = resultSet.getBytes(sqlKeyword);
      } else {
        value = resultSet.getObject(sqlKeyword);
      }

      if (resultSet.wasNull()) {
        value = null;
      }

      record.addColumn(column, value);
    }
    return record;
  }

}
