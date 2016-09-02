package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
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
import com.rapleaf.jack.tracking.NoOpAction;
import com.rapleaf.jack.tracking.PostQueryAction;
import com.rapleaf.jack.tracking.QueryStatistics;

public class GenericQuery {
  private static final Logger LOG = LoggerFactory.getLogger(GenericQuery.class);
  protected static int MAX_CONNECTION_RETRIES = 1;

  private final BaseDatabaseConnection dbConnection;
  private final List<TableReference> tableReferences;
  private final PostQueryAction postQueryAction;
  private final List<JoinCondition> joinConditions;
  private final List<GenericConstraint> whereConstraints;
  private final List<Object> parameters;
  private final List<OrderCriterion> orderCriteria;
  private final List<IndexHint> indexHints;
  private final Set<Column> selectedColumns;
  private final Set<Column> groupByColumns;
  private Optional<LimitCriterion> limitCriteria;

  private GenericQuery(BaseDatabaseConnection dbConnection, Table table, PostQueryAction postQueryAction) {
    this.dbConnection = dbConnection;
    this.tableReferences = Lists.<TableReference>newArrayList(new SingleTableReference(table));
    this.postQueryAction = postQueryAction;
    this.joinConditions = Lists.newArrayList();
    this.whereConstraints = Lists.newArrayList();
    this.parameters = Lists.newArrayList();
    this.orderCriteria = Lists.newArrayList();
    this.indexHints = Lists.newArrayList();
    this.selectedColumns = Sets.newHashSet();
    this.groupByColumns = Sets.newHashSet();
    this.limitCriteria = Optional.absent();
  }

  private GenericQuery(BaseDatabaseConnection dbConnection, TableReference tableReference, PostQueryAction postQueryAction) {
    this.dbConnection = dbConnection;
    this.tableReferences = Lists.newArrayList(tableReference);
    this.postQueryAction = postQueryAction;
    this.joinConditions = Lists.newArrayList();
    this.whereConstraints = Lists.newArrayList();
    this.parameters = Lists.newArrayList();
    this.orderCriteria = Lists.newArrayList();
    this.indexHints = Lists.newArrayList();
    this.selectedColumns = Sets.newHashSet();
    this.groupByColumns = Sets.newHashSet();
    this.limitCriteria = Optional.absent();
  }

  public static Builder create(BaseDatabaseConnection dbConnection) {
    return new Builder(dbConnection);
  }

  public static class Builder {
    private BaseDatabaseConnection dbConnection;
    private PostQueryAction postQueryAction = new NoOpAction();

    public Builder(BaseDatabaseConnection dbConnection) {
      this.dbConnection = dbConnection;
    }

    public Builder setConnection(BaseDatabaseConnection dbConnection) {
      this.dbConnection = dbConnection;
      return this;
    }

    public Builder setPostQueryAction(PostQueryAction action) {
      this.postQueryAction = action;
      return this;
    }

    public GenericQuery from(Table table) {
      return from(new SingleTableReference(table));
    }

    public GenericQuery from(TableReference tableReference) {
      return new GenericQuery(dbConnection, tableReference, postQueryAction);
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
    return leftJoin(new SingleTableReference(table));
  }

  public JoinConditionBuilder leftJoin(TableReference tableReference) {
    return new JoinConditionBuilder(this, JoinType.LEFT_JOIN, tableReference);
  }

  public JoinConditionBuilder rightJoin(Table table) {
    return rightJoin(new SingleTableReference(table));
  }

  public JoinConditionBuilder rightJoin(TableReference tableReference) {
    return new JoinConditionBuilder(this, JoinType.RIGHT_JOIN, tableReference);
  }

  public JoinConditionBuilder innerJoin(Table table) {
    return innerJoin(new SingleTableReference(table));
  }

  public JoinConditionBuilder innerJoin(TableReference tableReference) {
    return new JoinConditionBuilder(this, JoinType.INNER_JOIN, tableReference);
  }

  void addJoinCondition(JoinCondition joinCondition) {
    this.tableReferences.add(joinCondition.getTableReference());
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

  public GenericQuery useIndex(Index index, Index... indices) {
    this.indexHints.add(new IndexHint(IndexHint.Type.USE, IndexHint.Scope.ALL, index, indices));
    return this;
  }

  public GenericQuery useIndex(IndexHint.Scope hintScope, Index index, Index... indices) {
    this.indexHints.add(new IndexHint(IndexHint.Type.USE, hintScope, index, indices));
    return this;
  }

  public GenericQuery forceIndex(Index index, Index... indices) {
    this.indexHints.add(new IndexHint(IndexHint.Type.FORCE, IndexHint.Scope.ALL, index, indices));
    return this;
  }

  public GenericQuery forceIndex(IndexHint.Scope hintScope, Index index, Index... indices) {
    this.indexHints.add(new IndexHint(IndexHint.Type.FORCE, hintScope, index, indices));
    return this;
  }

  public GenericQuery ignoreIndex(Index index, Index... indices) {
    this.indexHints.add(new IndexHint(IndexHint.Type.IGNORE, IndexHint.Scope.ALL, index, indices));
    return this;
  }

  public GenericQuery ignoreIndex(IndexHint.Scope hintScope, Index index, Index... indices) {
    this.indexHints.add(new IndexHint(IndexHint.Type.IGNORE, hintScope, index, indices));
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

        final QueryStatistics statistics = statTracker.calculate();
        queryResults.addStatistics(statistics);

        try {
          postQueryAction.perform(statistics);
        } catch (Exception ignoredException) {
          LOG.error(String.format("Error occurred running post-query action %s", postQueryAction), ignoredException);
        }

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
    return QueryFetcher.getQueryResults(preparedStatement, selectedColumns, dbConnection) ;
  }

  private String getQueryStatement() {
    return getSelectClause()
        + getFromClause()
        + getJoinClause()
        + getWhereClause()
        + getGroupByClause()
        + getOrderClause()
        + getLimitClause()
        + getIndexHintClause();
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
      for (TableReference tableReference : tableReferences) {
        selectedColumns.addAll(tableReference.getTable().getAllColumns());
      }
    }
    return getClauseFromColumns(selectedColumns, "SELECT ", ", ", " ");
  }

  private String getFromClause() {
    return "FROM " + tableReferences.get(0).getSqlStatement() + " ";
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

  private String getIndexHintClause() {
    if (indexHints.isEmpty()) {
      return "";
    } else {
      return getClauseFromQueryConditions(indexHints, "", " ", " ");
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
