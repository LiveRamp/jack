package com.rapleaf.jack.generic_queries;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.ModelWithId;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class GenericQueryBuilder {

  private static int MAX_CONNECTION_RETRIES = 1;

  private final GenericQuery genericQuery;
  private final BaseDatabaseConnection dbConnection;

  public GenericQueryBuilder(BaseDatabaseConnection dbConnection, GenericQuery genericQuery) {
    this.dbConnection = dbConnection;
    this.genericQuery = genericQuery;
  }

  public GenericQueryBuilder join(Class<? extends ModelWithId> model, ModelField modelField1, ModelField modelField2) {
    genericQuery.addJoinCondition(new JoinCondition(model, modelField1, modelField2));
    return this;
  }

  public GenericQueryBuilder where(ModelField modelField, IWhereOperator operator) {
    genericQuery.addWhereCondition(new WhereCondition(modelField, operator));
    return this;
  }

  public GenericQueryBuilder orderBy(ModelField modelField, QueryOrder queryOrder) {
    genericQuery.addOrderCondition(new OrderCondition(modelField, queryOrder));
    return this;
  }

  public GenericQueryBuilder orderBy(ModelField modelField) {
    genericQuery.addOrderCondition(new OrderCondition(modelField, QueryOrder.ASC));
    return this;
  }

  public GenericQueryBuilder limit(int offset, int limit) {
    genericQuery.addLimitCondition(new LimitCondition(offset, limit));
    return this;
  }

  public GenericQueryBuilder limit(int limit) {
    genericQuery.addLimitCondition(new LimitCondition(0, limit));
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

  public List<Map<ModelField, Object>> fetch() throws IOException {
    int retryCount = 0;
    PreparedStatement preparedStatement;
    List<Map<ModelField, Object>> results = Lists.newArrayList();

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

  private void queryExecution(List<Map<ModelField, Object>> results, PreparedStatement preparedStatement) throws SQLException {
    ResultSet queryResultSet = null;

    try {
      queryResultSet = preparedStatement.executeQuery();
      while (queryResultSet.next()) {
        Map<ModelField, Object> fieldCollection = parseResultSet(queryResultSet);
        results.add(fieldCollection);
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
      }
    }
  }

  private Map<ModelField, Object> parseResultSet(ResultSet queryResultSet) throws SQLException{
    Set<ModelField> selectedModelFields = genericQuery.getSelectedIModelFields();
    Map<ModelField, Object> fieldCollection = Maps.newHashMapWithExpectedSize(selectedModelFields.size());

    for (ModelField modelField : selectedModelFields) {
      String sqlKeyword = modelField.getSqlKeyword();
      fieldCollection.put(modelField, queryResultSet.getObject(sqlKeyword));
    }

    return fieldCollection;
  }

  private void setStatementParameters(PreparedStatement preparedStatement) throws IOException {
    int index = 0;
    for (WhereCondition condition : genericQuery.getWhereConditions()) {
      for (Object parameter : condition.getParameters()) {
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
