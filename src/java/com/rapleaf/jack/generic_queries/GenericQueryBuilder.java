package com.rapleaf.jack.generic_queries;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.IModelField;
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

  public GenericQueryBuilder join(Class<? extends ModelWithId> model, IModelField IModelField1, IModelField IModelField2) {
    genericQuery.addJoinCondition(new JoinCondition(model, IModelField1, IModelField2));
    return this;
  }

  public GenericQueryBuilder where(IModelField IModelField, IWhereOperator operator) {
    genericQuery.addWhereCondition(new WhereCondition(IModelField, operator));
    return this;
  }

  public GenericQueryBuilder orderBy(IModelField IModelField, QueryOrder queryOrder) {
    genericQuery.addOrderCondition(new OrderCondition(IModelField, queryOrder));
    return this;
  }

  public GenericQueryBuilder orderBy(IModelField IModelField) {
    genericQuery.addOrderCondition(new OrderCondition(IModelField, QueryOrder.ASC));
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

  public GenericQueryBuilder select(IModelField... IModelFields) {
    for (IModelField IModelField : IModelFields) {
      genericQuery.addSelectedModelField(IModelField);
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
