package com.rapleaf.jack;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class AbstractQueryBuilder<M extends ModelWithId> implements IQueryBuilder<M> {

  private ModelQuery query;
  private IModelPersistence<M> caller;

  public AbstractQueryBuilder(IModelPersistence<M> caller) {
    this.caller = caller;
    this.query = new ModelQuery();
  }

  public void addWhereConstraint(WhereConstraint whereConstraint) {
    query.addConstraint(whereConstraint);
  }

  public void addOrder(OrderCriterion orderCriterion) {
    query.addOrder(orderCriterion);
  }

  public void setLimit(LimitCriterion limitCriterion) {
    query.setLimitCriterion(limitCriterion);
  }

  public void addSelectedField(FieldSelector field) {
    query.addSelectedField(field);
  }

  public void addSelectedFields(FieldSelector... fields) {
    query.addSelectedFields(fields);
  }

  public void addGroupByFields(Enum... fields) {
    query.addGroupByFields(fields);
  }

  public void addIds(Set<Long> ids) {
    query.addIds(ids);
  }

  public void addId(Long id) {
    query.addId(id);
  }

  @Override
  public Set<M> find() throws IOException {
    return caller.find(query);
  }

  @Override
  public List<M> findWithOrder() throws IOException {
    return caller.findWithOrder(query);
  }
}
