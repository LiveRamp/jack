package com.rapleaf.jack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbstractQueryBuilder<M extends ModelWithId> implements IQueryBuilder<M> {

  private List<QueryConstraint> constraints;
  private Set<Long> ids;
  private IModelPersistence<M> caller;

  public AbstractQueryBuilder(IModelPersistence<M> caller) {
    this.caller = caller;
    this.constraints = new ArrayList<QueryConstraint>();
    this.ids = new HashSet<Long>();
  }

  public void addConstraint(QueryConstraint constraint) {
    constraints.add(constraint);
  }

  public void addIds(Set<Long> ids) {
    this.ids.addAll(ids);
  }

  public void addId(Long id) {
    ids.add(id);
  }

  @Override
  public Set<M> find() throws IOException {
    return caller.find(ids, constraints);
  }
}
