package com.rapleaf.jack;

import java.io.IOException;
import java.util.Set;

public class AbstractQueryBuilder<M extends ModelWithId> implements IQueryBuilder<M> {

  private ModelQuery query;
  private IModelPersistence<M> caller;

  public AbstractQueryBuilder(IModelPersistence<M> caller) {
    this.caller = caller;
    this.query = new ModelQuery();
  }

  public void addConstraint(QueryConstraint constraint) {
    query.addConstraint(constraint);
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
}
