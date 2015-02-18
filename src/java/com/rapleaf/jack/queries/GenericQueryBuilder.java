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
import com.rapleaf.jack.ModelField;
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

  public JoinConditionBuilder leftJoin(Class<? extends ModelWithId> model) {
    return new JoinConditionBuilder(this, JoinType.LEFT_JOIN, model);
  }

  public JoinConditionBuilder rightJoin(Class<? extends ModelWithId> model) {
    return new JoinConditionBuilder(this, JoinType.RIGHT_JOIN, model);
  }

  public JoinConditionBuilder innerJoin(Class<? extends ModelWithId> model) {
    return new JoinConditionBuilder(this, JoinType.INNER_JOIN, model);
  }

  GenericQueryBuilder addJoinCondition(JoinCondition joinCondition) {
    genericQuery.addJoinCondition(joinCondition);
    return this;
  }

  public GenericQueryBuilder where(ModelField modelField, IWhereOperator<Object> operator) {
    genericQuery.addWhereCondition(new WhereConstraint<Object>(modelField, operator, null));
    return this;
  }

  public GenericQueryBuilder and(ModelField modelField, IWhereOperator<Object> operator) {
    genericQuery.addWhereCondition(new WhereConstraint<Object>(modelField, operator, WhereConstraint.Logic.AND));
    return this;
  }

  public GenericQueryBuilder or(ModelField modelField, IWhereOperator<Object> operator) {
    genericQuery.addWhereCondition(new WhereConstraint<Object>(modelField, operator, WhereConstraint.Logic.OR));
    return this;
  }

  public GenericQueryBuilder orderBy(ModelField modelField, QueryOrder queryOrder) {
    genericQuery.addOrderCondition(new OrderCriterion(modelField, queryOrder));
    return this;
  }

  public GenericQueryBuilder orderBy(ModelField modelField) {
    genericQuery.addOrderCondition(new OrderCriterion(modelField, QueryOrder.ASC));
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

  public GenericQueryBuilder select(ModelField... modelFields) {
    for (ModelField modelField : modelFields) {
      genericQuery.addSelectedModelField(modelField);
    }
    return this;
  }

  public String getSqlStatement() {
    return genericQuery.getSqlStatement();
  }

  public PreparedStatement getPreparedStatement() throws IOException {
    PreparedStatement preparedStatement = dbConnection.getPreparedStatement(getSqlStatement());
    setStatementParameters(preparedStatement);
    return preparedStatement;
  }

  public List<QueryEntry> fetch() throws IOException {
    int retryCount = 0;
    PreparedStatement preparedStatement;
    List<QueryEntry> results = Lists.newArrayList();

    while (true) {
      preparedStatement = getPreparedStatement();
      try {
        queryExecution(results, preparedStatement);
        return results;
      } catch (SQLRecoverableException e) {
        if (++retryCount > MAX_CONNECTION_RETRIES) {
          throw new IOException(e);
        }
      } catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }

  private void queryExecution(List<QueryEntry> results, PreparedStatement preparedStatement) throws SQLException {
    ResultSet queryResultSet = null;

    try {
      queryResultSet = preparedStatement.executeQuery();
      while (queryResultSet.next()) {
        QueryEntry fieldCollection = parseResultSet(queryResultSet);
        if (fieldCollection != null) {
          results.add(fieldCollection);
        }
      }
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
    Set<ModelField> selectedModelFields = genericQuery.getSelectedModelFields();
    if (selectedModelFields.isEmpty()) {
      return null;
    }

    QueryEntry queryEntry = new QueryEntry(selectedModelFields.size());
    for (ModelField modelField : selectedModelFields) {
      String sqlKeyword = modelField.getSqlKeyword();
      Object value = queryResultSet.getObject(sqlKeyword);
      value = queryResultSet.wasNull() ? null : value;
      queryEntry.addModelField(modelField, value);
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
