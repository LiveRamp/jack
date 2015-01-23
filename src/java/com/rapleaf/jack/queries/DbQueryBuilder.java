package com.rapleaf.jack.queries;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.IModelPersistence;
import com.rapleaf.jack.ModelWithId;

public class DbQueryBuilder {

  private final ModelQuery query;
  private final BaseDatabaseConnection conn;

  public DbQueryBuilder(BaseDatabaseConnection conn, Class<? extends ModelWithId> model) {
    this.query = new ModelQuery(model);
    this.conn = conn;
  }

  protected DbQueryBuilder where(WhereConstraint whereConstraint) {
    query.addConstraint(whereConstraint);
    return this;
  }

  protected DbQueryBuilder orderBy(OrderCriterion orderCriterion) {
    query.addOrder(orderCriterion);
    return this;
  }

  protected DbQueryBuilder limit(LimitCriterion limitCriterion) {
    query.setLimitCriterion(limitCriterion);
    return this;
  }

  protected DbQueryBuilder join(JoinCondition joinCondition) {
    query.addJoinCondition(joinCondition);
    return this;
  }

  public Set<Map<Class<? extends ModelWithId>, Map<Enum, Object>>> find() throws IOException {
    return null;
  }

  public List<Map<Class<? extends ModelWithId>, Map<Enum, Object>>> findWithOrder() throws IOException {
    return null;
  }
}
