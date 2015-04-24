package com.rapleaf.jack.queries;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.rapleaf.jack.IModelPersistence;
import com.rapleaf.jack.ModelWithId;

public abstract class AbstractQueryBuilder<M extends ModelWithId> implements IQueryBuilder<M> {

  private ModelQuery query;
  private IModelPersistence<M> caller;

  public AbstractQueryBuilder(IModelPersistence<M> caller) {
    this.caller = caller;
    this.query = new ModelQuery();
  }

  protected void addWhereConstraint(WhereConstraint whereConstraint) {
    query.addConstraint(whereConstraint);
  }

  protected void addOrder(OrderCriterion orderCriterion) {
    query.addOrder(orderCriterion);
  }

  protected void setLimit(LimitCriterion limitCriterion) {
    query.setLimitCriterion(limitCriterion);
  }

  protected void addSelectedField(FieldSelector field) {
    query.addSelectedField(field);
  }

  protected void addSelectedFields(FieldSelector... fields) {
    query.addSelectedFields(fields);
  }

  protected void addGroupByFields(Enum... fields) {
    query.addGroupByFields(fields);
  }

  protected void addIds(Set<Long> ids) {
    query.addIds(ids);
  }

  protected void addId(Long id) {
    query.addId(id);
  }

  @Override
  public List<M> find() throws IOException {
    return caller.findWithOrder(query);
  }
}
