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

  public String getSqlStatement(boolean isOrderedQuery) {
    return genericQuery.getSqlStatement(isOrderedQuery);
  }

  public List<Map<ModelField, Object>> fetch() throws IOException {
    int retryCount = 0;
    PreparedStatement preparedStatement;
    List<Map<ModelField, Object>> results = Lists.newArrayList();

    while (true) {
      preparedStatement = getPreparedStatement(genericQuery.isOrderedQuery());
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

  private PreparedStatement getPreparedStatement(boolean isOrderedQuery) throws IOException {
    PreparedStatement preparedStatement = dbConnection.getPreparedStatement(getSqlStatement(isOrderedQuery));
    setStatementParameters(preparedStatement);
    return preparedStatement;
  }

  private void setStatementParameters(PreparedStatement preparedStatement) throws IOException {
    int index = 0;
    for (WhereCondition condition : genericQuery.getWhereConditions()) {
      Class fieldType = condition.getModelFieldType();
      for (Object parameter : condition.getParameters()) {
        if (parameter == null) {
          continue;
        }
        try {
          if (fieldType == Integer.class) {
            preparedStatement.setInt(++index, (Integer)parameter);
          } else if (fieldType == String.class) {
            preparedStatement.setString(++index, (String)parameter);
          } else if (fieldType == Long.class) {
            preparedStatement.setLong(++index, (Long)parameter);
          } else if (fieldType == byte[].class) {
            preparedStatement.setBytes(++index, (byte[])parameter);
          } else if (fieldType == Double.class) {
            preparedStatement.setDouble(++index, (Double)parameter);
          } else if (fieldType == Boolean.class) {
            preparedStatement.setBoolean(++index, (Boolean)parameter);
          } else {
            throw new RuntimeException("Unsupported field type " + fieldType.getSimpleName());
          }
        } catch (SQLException e) {
          throw new IOException(e);
        }
      }
    }
  }
}
