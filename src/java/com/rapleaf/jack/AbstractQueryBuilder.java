package com.rapleaf.jack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AbstractQueryBuilder<M extends ModelWithId> implements IQueryBuilder<M> {

  private List<QueryConstraint> constraints;
  private IModelPersistence<M> caller;

  public AbstractQueryBuilder(IModelPersistence<M> caller) {
    this.caller = caller;
    this.constraints = new ArrayList<QueryConstraint>();
  }

  public void addConstraint(QueryConstraint constraint) {
    constraints.add(constraint);
  }

  @Override
  public Set<M> find() throws IOException {
    return caller.find(constraints);
  }
}
