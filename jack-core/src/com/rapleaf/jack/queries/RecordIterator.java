package com.rapleaf.jack.queries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.tracking.QueryStatistics;

public class RecordIterator implements Iterator<Record>, AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(RecordIterator.class);

  private PreparedStatement preparedStatement;
  private Set<Column> selectedColumns;

  private ResultSet resultSet;
  private BaseDatabaseConnection conn;
  private boolean hasNext;

  private QueryStatistics queryStatistics;
  private QueryStatistics.Measurer measurer;

  RecordIterator(PreparedStatement preparedStatement, Set<Column> selectedColumns, ResultSet resultSet, BaseDatabaseConnection conn) {
    this.preparedStatement = preparedStatement;
    this.selectedColumns = selectedColumns;
    this.resultSet = resultSet;
    this.conn = conn;
    this.queryStatistics = null;
    updateHasNext();
  }

  void addStatisticsMeasurer(QueryStatistics.Measurer measurer) {
    this.measurer = measurer;
  }

  private void updateHasNext() {
    try {
      this.hasNext = resultSet.next();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Record getNext() {
    try {
      return QueryFetcher.parseResultSet(resultSet, selectedColumns);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean hasNext() {
    return hasNext;
  }

  @Override
  public Record next() {
    if (!hasNext) {
      throw new NoSuchElementException();
    }
    Record record = getNext();
    updateHasNext();
    return record;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() {
    try {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      if (resultSet != null) {
        resultSet.close();
      }
      QueryFetcher.closeQuery(resultSet, preparedStatement, conn);
      measurer.recordQueryExecEnd();
      this.queryStatistics = measurer.calculate();
    } catch (SQLException e) {
      LOG.error("RecordIterator close operation failed", e);
    }
  }

  public QueryStatistics getQueryStatistics() {
    return queryStatistics;
  }
}
