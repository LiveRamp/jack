package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.Column;
import com.rapleaf.jack.ModelTable;
import com.rapleaf.jack.ModelWithId;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class GenericQueryBuilder {
  private static int MAX_CONNECTION_RETRIES = 1;

  private final GenericQuery genericQuery;
  private final BaseDatabaseConnection dbConnection;

  public GenericQueryBuilder(BaseDatabaseConnection dbConnection, GenericQuery genericQuery) {
    this.dbConnection = dbConnection;
    this.genericQuery = genericQuery;
  }

  public JoinConditionBuilder leftJoin(ModelTable table) {
    return new JoinConditionBuilder(this, JoinType.LEFT_JOIN, table);
  }

  public JoinConditionBuilder rightJoin(ModelTable table) {
    return new JoinConditionBuilder(this, JoinType.RIGHT_JOIN, table);
  }

  public JoinConditionBuilder innerJoin(ModelTable table) {
    return new JoinConditionBuilder(this, JoinType.INNER_JOIN, table);
  }

  GenericQueryBuilder addJoinCondition(JoinCondition joinCondition) {
    genericQuery.addJoinCondition(joinCondition);
    return this;
  }

  public GenericQueryBuilder where(Column column, IWhereOperator operator) {
    genericQuery.addWhereCondition(new WhereConstraint(column, operator, null));
    return this;
  }

  public GenericQueryBuilder and(Column column, IWhereOperator operator) {
    genericQuery.addWhereCondition(new WhereConstraint(column, operator, WhereConstraint.Logic.AND));
    return this;
  }

  public GenericQueryBuilder or(Column column, IWhereOperator operator) {
    genericQuery.addWhereCondition(new WhereConstraint(column, operator, WhereConstraint.Logic.OR));
    return this;
  }

  public GenericQueryBuilder orderBy(Column column, QueryOrder queryOrder) {
    genericQuery.addOrderCondition(new OrderCriterion(column, queryOrder));
    return this;
  }

  public GenericQueryBuilder orderBy(Column column) {
    genericQuery.addOrderCondition(new OrderCriterion(column, QueryOrder.ASC));
    return this;
  }

  public GenericQueryBuilder limit(int offset, int limit) {
    genericQuery.addLimitCondition(new LimitCriterion(offset, limit));
    return this;
  }

  public GenericQueryBuilder limit(int limit) {
    genericQuery.addLimitCondition(new LimitCriterion(limit));
    return this;
  }

  public GenericQueryBuilder groupBy(Column column, Column... columns) {
    genericQuery.addGroupByModelFields(column, columns);
    return this;
  }

  public GenericQueryBuilder select(Column column, Column... columns) {
    genericQuery.addSelectedModelField(column, columns);
    return this;
  }

  public PreparedStatement getPreparedStatement() throws IOException {
    PreparedStatement preparedStatement = dbConnection.getPreparedStatement(genericQuery.getSqlStatement());
    setStatementParameters(preparedStatement);
    return preparedStatement;
  }

  public List<QueryEntry> fetch() throws IOException {
    int retryCount = 0;
    PreparedStatement preparedStatement = getPreparedStatement();

    while (true) {
      try {
        return getQueryResults(preparedStatement);
      } catch (SQLRecoverableException e) {
        if (++retryCount > MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  private List<QueryEntry> getQueryResults(PreparedStatement preparedStatement) throws SQLException {
    ResultSet queryResultSet = null;

    try {
      queryResultSet = preparedStatement.executeQuery();
      List<QueryEntry> results = Lists.newArrayList();
      while (queryResultSet.next()) {
        QueryEntry fieldCollection = parseResultSet(queryResultSet);
        if (fieldCollection != null) {
          results.add(fieldCollection);
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
        dbConnection.resetConnection();
      } catch (SQLException e) {
        // ignore
      }
    }
  }

  private QueryEntry parseResultSet(ResultSet queryResultSet) throws SQLException{
    Set<Column> selectedColumns = genericQuery.getSelectedColumns();
    if (selectedColumns.isEmpty()) {
      return null;
    }

    QueryEntry queryEntry = new QueryEntry(selectedColumns.size());
    for (Column column : selectedColumns) {
      String sqlKeyword = column.getSqlKeyword();
      Object value = queryResultSet.getObject(sqlKeyword);
      value = queryResultSet.wasNull() ? null : value;
      queryEntry.addModelField(column, value);
    }
    return queryEntry;
  }

  private void setStatementParameters(PreparedStatement preparedStatement) throws IOException {
    int index = 0;
    for (WhereConstraint constraint : genericQuery.getWhereConstraints()) {
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
}
