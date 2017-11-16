package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

import com.rapleaf.jack.BaseDatabaseConnection;

public class QueryFetcher extends BaseFetcher {

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
      throw new RuntimeException(e);
    } finally {
      closeQuery(resultSet, preparedStatement, dbConnection);
    }
  }

  public static Stream<Record> getQueryResultsStream(PreparedStatement preparedStatement, Set<Column> selectedColumns, BaseDatabaseConnection dbConnection) throws SQLException {
    try {
      ResultSet resultSet = preparedStatement.executeQuery();
      boolean firstRecordValid = resultSet.next();

      UnmodifiableIterator<Record> nonNullRecords = Iterators.filter(new Iterator<Record>() {
        private boolean hasNext = firstRecordValid;
        private ResultSet results = resultSet;

        @Override
        public boolean hasNext() {
          return hasNext;
        }

        @Override
        public Record next() {
          try {
            Record record = parseResultSet(resultSet, selectedColumns);
            hasNext = results.next();
            if (!hasNext) {
              closeQuery(results, preparedStatement, dbConnection);
            }
            return record;
          } catch (SQLRecoverableException recoverable) {
            dbConnection.resetConnection();
            throw new RuntimeException(recoverable);
          } catch (SQLException e) {
            throw new RuntimeException(e);
          }
        }
      }, r -> r != null);

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
      closeQuery(resultSet, preparedStatement, dbConnection);
    }
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
