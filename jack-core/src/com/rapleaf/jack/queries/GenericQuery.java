package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.tracking.NoOpAction;
import com.rapleaf.jack.tracking.PostQueryAction;
import com.rapleaf.jack.tracking.QueryStatistics;

public class GenericQuery extends AbstractExecution {
  private static final Logger LOG = LoggerFactory.getLogger(GenericQuery.class);
  protected static int MAX_CONNECTION_RETRIES = 1;

  private final List<Table> includedTables;
  private final PostQueryAction postQueryAction;
  private final List<JoinCondition> joinConditions;
  private final List<GenericConstraint> whereConstraints;
  private final List<Object> parameters;
  private final List<OrderCriterion> orderCriteria;
  private final Set<Column> selectedColumns;
  private final Set<Column> groupByColumns;
  private Optional<LimitCriterion> limitCriteria;
  private boolean selectDistinct;

  private GenericQuery(BaseDatabaseConnection dbConnection, Table table, PostQueryAction postQueryAction) {
    super(dbConnection);
    this.includedTables = Lists.newArrayList(table);
    this.postQueryAction = postQueryAction;
    this.joinConditions = Lists.newArrayList();
    this.whereConstraints = Lists.newArrayList();
    this.parameters = Lists.newArrayList();
    this.orderCriteria = Lists.newArrayList();
    this.selectedColumns = Sets.newHashSet();
    this.groupByColumns = Sets.newHashSet();
    this.limitCriteria = Optional.empty();
    this.selectDistinct = false;
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
      return new GenericQuery(dbConnection, table, postQueryAction);
    }
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

  public GenericQuery distinct() {
    this.selectDistinct = true;
    return this;
  }

  public Records fetch() throws IOException {
    int retryCount = 0;
    final QueryStatistics.Measurer statTracker = new QueryStatistics.Measurer();
    statTracker.recordQueryPrepStart();
    PreparedStatement preparedStatement = getPreparedStatement(Optional.empty());
    statTracker.recordQueryPrepEnd();

    while (true) {
      try {
        statTracker.recordAttempt();

        statTracker.recordQueryExecStart();
        final Records queryResults = QueryFetcher.getQueryResults(preparedStatement, selectedColumns, dbConnection);
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

  public Stream<Record> fetchAsStream() throws IOException {
    int retryCount = 0;
    final QueryStatistics.Measurer statTracker = new QueryStatistics.Measurer();
    statTracker.recordQueryPrepStart();
    PreparedStatement preparedStatement = getPreparedStatement(Optional.empty());
    statTracker.recordQueryPrepEnd();
    while (true) {
      try {
        statTracker.recordQueryExecStart();
        RecordIterator itr =
            QueryFetcher.getQueryResultsStream(preparedStatement, selectedColumns, dbConnection);
        itr.addStatisticsMeasurer(statTracker);
        Iterable<Record> i = () -> itr;
        Stream<Record> stream = StreamSupport.stream(i.spliterator(), false);
        stream = stream.onClose(() -> itr.close());
        return stream;
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

  @Override
  public String getQueryStatement() {
    return getSelectClause()
        + getFromClause()
        + getJoinClause()
        + getWhereClause()
        + getGroupByClause()
        + getOrderClause()
        + getLimitClause();
  }

  @Override
  public Collection<Object> getParameters() {
    return parameters;
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
    String initialKeyword = selectDistinct ? "SELECT DISTINCT " : "SELECT ";
    return getClauseFromColumns(selectedColumns, initialKeyword, ", ", " ");
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

}
