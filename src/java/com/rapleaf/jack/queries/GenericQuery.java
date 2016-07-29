package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.tracking.QueryStatistics;

public class GenericQuery {
  private static final Logger LOG = LoggerFactory.getLogger(GenericQuery.class);
  protected static int MAX_CONNECTION_RETRIES = 1;

  private final BaseDatabaseConnection dbConnection;
  private final List<Table> includedTables;
  private final List<JoinCondition> joinConditions;
  private final List<GenericConstraint> whereConstraints;
  private final List<Object> parameters;
  private final List<OrderCriterion> orderCriteria;
  private final Set<Column> selectedColumns;
  private final Set<Column> groupByColumns;
  private Optional<LimitCriterion> limitCriteria;

  private GenericQuery(BaseDatabaseConnection dbConnection, Table table) {
    this.dbConnection = dbConnection;
    this.includedTables = Lists.newArrayList(table);
    this.joinConditions = Lists.newArrayList();
    this.whereConstraints = Lists.newArrayList();
    this.parameters = Lists.newArrayList();
    this.orderCriteria = Lists.newArrayList();
    this.selectedColumns = Sets.newHashSet();
    this.groupByColumns = Sets.newHashSet();
    this.limitCriteria = Optional.absent();
  }

  public static Builder create(BaseDatabaseConnection dbConnection) {
    return new Builder(dbConnection);
  }

  public static class Builder {
    private BaseDatabaseConnection dbConnection;

    public Builder(BaseDatabaseConnection dbConnection) {
      this.dbConnection = dbConnection;
    }

    public Builder setConnection(BaseDatabaseConnection dbConnection) {
      this.dbConnection = dbConnection;
      return this;
    }

    public GenericQuery from(Table table) {
      return new GenericQuery(dbConnection, table);
    }
  }

  public void setAutoCommit(boolean autoCommit) {
    this.dbConnection.setAutoCommit(autoCommit);
  }

  public boolean getAutoCommit() {
    return this.dbConnection.getAutoCommit();
  }

  public void commit() {
    this.dbConnection.commit();
  }

  public void rollback() {
    this.dbConnection.rollback();
  }

  public void resetConnection() {
    this.dbConnection.resetConnection();
  }

  public JoinConditionBuilder leftJoin(Table table) {
    return new JoinConditionBuilder(this, JoinType.LEFT_JOIN, table);
  }

  public JoinConditionBuilder rightJoin(Table table) {
    return new JoinConditionBuilder(this, JoinType.RIGHT_JOIN, table);
  }

  public JoinConditionBuilder innerJoin(Table table) {
    return new JoinConditionBuilder(this, JoinType.INNER_JOIN, table);
  }

  void addJoinCondition(JoinCondition joinCondition) {
    this.includedTables.add(joinCondition.getTable());
    this.joinConditions.add(joinCondition);
  }

  void addParameters(List parameters) {
    this.parameters.addAll(parameters);
  }

  public GenericQuery where(GenericConstraint constraint, GenericConstraint... constraints) {
    this.whereConstraints.add(constraint);
    this.parameters.addAll(constraint.getParameters());
    for (GenericConstraint genericConstraint : constraints) {
      this.whereConstraints.add(genericConstraint);
      this.parameters.addAll(genericConstraint.getParameters());
    }
    return this;
  }

  public GenericQuery orderBy(Column column, QueryOrder queryOrder) {
    this.orderCriteria.add(new OrderCriterion(column, queryOrder));
    return this;
  }

  public GenericQuery orderBy(Column column) {
    this.orderCriteria.add(new OrderCriterion(column, QueryOrder.ASC));
    return this;
  }

  public GenericQuery limit(int offset, int limit) {
    this.limitCriteria = Optional.of(new LimitCriterion(offset, limit));
    return this;
  }

  public GenericQuery limit(int limit) {
    this.limitCriteria = Optional.of(new LimitCriterion(limit));
    return this;
  }

  public GenericQuery groupBy(Collection<Column> columns) {
    this.groupByColumns.addAll(columns);
    return this;
  }

  public GenericQuery groupBy(Column column, Column... columns) {
    this.groupByColumns.add(column);
    this.groupByColumns.addAll(Arrays.asList(columns));
    return this;
  }

  public GenericQuery select(Collection<Column> columns) {
    this.selectedColumns.addAll(columns);
    return this;
  }

  public GenericQuery select(Column column, Column... columns) {
    this.selectedColumns.add(column);
    this.selectedColumns.addAll(Arrays.asList(columns));
    return this;
  }

  public String getSqlStatement() throws IOException {
    return getPreparedStatement().toString();
  }

  public Records fetch() throws IOException {
    int retryCount = 0;
    final QueryStatistics.Measurer statTracker = new QueryStatistics.Measurer();
    statTracker.recordQueryPrepStart();
    PreparedStatement preparedStatement = getPreparedStatement();
    statTracker.recordQueryPrepEnd();

    while (true) {
      try {
        statTracker.recordAttempt();

        statTracker.recordQueryExecStart();
        final Records queryResults = getQueryResults(preparedStatement);
        statTracker.recordQueryExecEnd();

        queryResults.addStatistics(statTracker.calculate());
        return queryResults;
      } catch (SQLRecoverableException e) {
        LOG.error(e.toString());
        if (++retryCount > MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  private PreparedStatement getPreparedStatement() throws IOException {
    PreparedStatement preparedStatement = dbConnection.getPreparedStatement(getQueryStatement());
    setStatementParameters(preparedStatement);
    return preparedStatement;
  }

  private void setStatementParameters(PreparedStatement preparedStatement) throws IOException {
    int index = 0;
    for (Object parameter : parameters) {
      if (parameter == null) {
        continue;
      }
      try {
        preparedStatement.setObject(++index, parameter);
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  private Records getQueryResults(PreparedStatement preparedStatement) throws SQLException {
    ResultSet resultSet = null;

    try {
      resultSet = preparedStatement.executeQuery();
      Records results = new Records();
      while (resultSet.next()) {
        Record record = parseResultSet(resultSet);
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

  private Record parseResultSet(ResultSet resultSet) throws SQLException {
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

  private String getQueryStatement() {
    return getSelectClause()
        + getFromClause()
        + getJoinClause()
        + getWhereClause()
        + getGroupByClause()
        + getOrderClause()
        + getLimitClause();
  }

  private String getSelectClause() {
    if (!groupByColumns.isEmpty()) {
      if (selectedColumns.isEmpty()) {
        throw new RuntimeException("The SELECT list cannot be empty when the GROUP BY clause is specified.");
      }

      for (Column column : selectedColumns) {
        if (!groupByColumns.contains(column) && !(column instanceof AggregatedColumn)) {
          throw new RuntimeException("The non-aggregated column " + column.getSqlKeyword() +
              " not named in the GROUP BY clause cannot be included in the SELECT list.");
        }
      }
    }

    if (selectedColumns.isEmpty()) {
      for (Table table : includedTables) {
        selectedColumns.addAll(table.getAllColumns());
      }
    }
    return getClauseFromColumns(selectedColumns, "SELECT ", ", ", " ");
  }

  private String getFromClause() {
    return "FROM " + includedTables.get(0).getSqlKeyword() + " ";
  }

  private String getJoinClause() {
    return getClauseFromQueryConditions(joinConditions, "", "", " ");
  }

  private String getWhereClause() {
    return getClauseFromQueryConditions(whereConstraints, "WHERE ", " AND ", " ");
  }

  private String getGroupByClause() {
    return getClauseFromColumns(groupByColumns, "GROUP BY ", ", ", " ");
  }

  private String getOrderClause() {
    if (orderCriteria.isEmpty()) {
      return "";
    } else {
      return getClauseFromQueryConditions(orderCriteria, "ORDER BY ", ", ", " ");
    }
  }

  private String getLimitClause() {
    if (limitCriteria.isPresent()) {
      return limitCriteria.get().getSqlStatement() + " ";
    } else {
      return "";
    }
  }

  static String getClauseFromColumns(Collection<Column> columns, String initialKeyword, String separator, String terminalKeyword) {
    if (columns.isEmpty()) {
      return "";
    }

    StringBuilder clause = new StringBuilder(initialKeyword);
    Iterator<Column> it = columns.iterator();
    while (it.hasNext()) {
      clause.append(it.next().getSqlKeyword());
      if (it.hasNext()) {
        clause.append(separator);
      }
    }

    return clause.append(terminalKeyword).toString();
  }

  static <T extends QueryCondition> String getClauseFromQueryConditions(Collection<T> conditions, String initialKeyword, String separator, String terminalKeyword) {
    if (conditions.isEmpty()) {
      return "";
    }

    StringBuilder clause = new StringBuilder(initialKeyword);
    Iterator<T> it = conditions.iterator();
    while (it.hasNext()) {
      clause.append(it.next().getSqlStatement());
      if (it.hasNext()) {
        clause.append(separator);
      }
    }

    return clause.append(terminalKeyword).toString();
  }
}
