package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
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
import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public abstract class AbstractGenericQuery {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractGenericQuery.class);
  protected static int MAX_CONNECTION_RETRIES = 1;

  private final BaseDatabaseConnection dbConnection;
  private final List<Table> includedTables;
  private final List<JoinCondition> joinConditions;
  private final List<WhereConstraint> whereConstraints;
  private final List<OrderCriterion> orderCriteria;
  private final Set<Column> selectedColumns;
  private final Set<Column> groupByColumns;
  private Optional<LimitCriterion> limitCriteria;

  protected AbstractGenericQuery(BaseDatabaseConnection dbConnection, Table table) {
    this.dbConnection = dbConnection;
    this.includedTables = Lists.newArrayList(table);
    this.joinConditions = Lists.newArrayList();
    this.whereConstraints = Lists.newArrayList();
    this.orderCriteria = Lists.newArrayList();
    this.selectedColumns = Sets.newHashSet();
    this.groupByColumns = Sets.newHashSet();
    this.limitCriteria = Optional.absent();
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

  public <T> AbstractGenericQuery where(Column column, IWhereOperator<T> operator) {
    addWhereCondition(new WhereConstraint<T>(column, operator, WhereConstraint.Logic.AND));
    return this;
  }

  public <T> AbstractGenericQuery andWhere(Column column, IWhereOperator<T> operator) {
    addWhereCondition(new WhereConstraint<T>(column, operator, WhereConstraint.Logic.AND));
    return this;
  }

  public <T> AbstractGenericQuery orWhere(Column column, IWhereOperator<T> operator) {
    addWhereCondition(new WhereConstraint<T>(column, operator, WhereConstraint.Logic.OR));
    return this;
  }

  private void addWhereCondition(WhereConstraint whereConstraint) {
    if (whereConstraints.isEmpty()) {
      // the first WHERE constraint cannot specify a logic
      whereConstraint.setLogic(null);
    } else if (whereConstraint.getLogic() == null) {
      // any non-first WHERE constraint without a logic will default to AND
      whereConstraint.setLogic(WhereConstraint.Logic.AND);
    }
    this.whereConstraints.add(whereConstraint);
  }

  public AbstractGenericQuery orderBy(Column column, QueryOrder queryOrder) {
    this.orderCriteria.add(new OrderCriterion(column, queryOrder));
    return this;
  }

  public AbstractGenericQuery orderBy(Column column) {
    this.orderCriteria.add(new OrderCriterion(column, QueryOrder.ASC));
    return this;
  }

  public AbstractGenericQuery limit(int offset, int limit) {
    this.limitCriteria = Optional.of(new LimitCriterion(offset, limit));
    return this;
  }

  public AbstractGenericQuery limit(int limit) {
    this.limitCriteria = Optional.of(new LimitCriterion(limit));
    return this;
  }

  public AbstractGenericQuery groupBy(Column column, Column... columns) {
    this.groupByColumns.add(column);
    this.groupByColumns.addAll(Arrays.asList(columns));
    return this;
  }

  public AbstractGenericQuery select(Column column, Column... columns) {
    this.selectedColumns.add(column);
    this.selectedColumns.addAll(Arrays.asList(columns));
    return this;
  }

  public String getSqlStatement() throws IOException {
    return getPreparedStatement().toString();
  }

  public Records fetch() throws IOException {
    int retryCount = 0;
    PreparedStatement preparedStatement = getPreparedStatement();

    while (true) {
      try {
        return getQueryResults(preparedStatement);
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
    for (WhereConstraint constraint : whereConstraints) {
      for (Object parameter : constraint.getParameters()) {
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
  }

  private Records getQueryResults(PreparedStatement preparedStatement) throws SQLException {
    ResultSet queryResultSet = null;

    try {
      queryResultSet = preparedStatement.executeQuery();
      Records results = new Records();
      while (queryResultSet.next()) {
        Record record = parseResultSet(queryResultSet);
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
        if (queryResultSet != null) {
          queryResultSet.close();
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

  private Record parseResultSet(ResultSet queryResultSet) throws SQLException{
    if (selectedColumns.isEmpty()) {
      return null;
    }

    Record record = new Record(selectedColumns.size());
    for (Column column : selectedColumns) {
      String sqlKeyword = column.getSqlKeyword();
      Object value = queryResultSet.getObject(sqlKeyword);
      value = queryResultSet.wasNull() ? null : value;
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
    return getClauseFromColumns(selectedColumns, "SELECT ", ", ");
  }

  private String getFromClause() {
    return "FROM " + includedTables.get(0).getSqlKeyword() + " ";
  }

  private String getJoinClause() {
    return getClauseFromQueryConditions(joinConditions, "", " ");
  }

  private String getWhereClause() {
    return getClauseFromQueryConditions(whereConstraints, "WHERE ", " ");
  }

  private String getGroupByClause() {
    return getClauseFromColumns(groupByColumns, "GROUP BY ", ", ");
  }

  private String getOrderClause() {
    if (orderCriteria.isEmpty()) {
      return "";
    } else {
      return getClauseFromQueryConditions(orderCriteria, "ORDER BY ", ", ");
    }
  }

  private String getLimitClause() {
    if (limitCriteria.isPresent()) {
      return limitCriteria.get().getSqlStatement() + " ";
    } else {
      return "";
    }
  }

  private String getClauseFromColumns(Collection<Column> columns, String initialKeyword, String separator) {
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

    return clause.append(" ").toString();
  }

  private <T extends QueryCondition> String getClauseFromQueryConditions(Collection<T> conditions, String initialKeyword, String separator) {
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

    return clause.append(" ").toString();
  }
}
