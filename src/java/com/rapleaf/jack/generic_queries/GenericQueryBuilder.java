package com.rapleaf.jack.generic_queries;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.ModelWithId;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class GenericQueryBuilder {

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

  public Set<Map<Class<? extends ModelWithId>, Map<Enum, Object>>> find() throws IOException {
    return null;
  }

  public List<Map<Class<? extends ModelWithId>, Map<Enum, Object>>> findWithOrder() throws IOException {
    return null;
  }
}
