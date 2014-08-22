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

  public void addConstraint(WhereConstraint constraint) {
    query.addConstraint(constraint);
  }

  public void addOrder(OrderCriterion orderCriterion) {
    query.addOrder(orderCriterion);
  }

  public void setLimit(LimitCriterion limitCriterion) {
    query.setLimitCriterion(limitCriterion);
  }

  public void addSelectedFields(Enum... fields) {
    query.addSelectedFields(fields);
  }

  public void addAggregatedFields(AggregatorFunction... aggregators) {
    query.addAggregatedFields(aggregators);
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
