package com.rapleaf.jack.queries;

import java.io.IOException;
import java.util.Collection;

import com.rapleaf.jack.IModelPersistence;
import com.rapleaf.jack.ModelWithId;

public abstract class AbstractDeleteBuilder<M extends ModelWithId> {
  private ModelDelete delete;
  private IModelPersistence<M> caller;

  public AbstractDeleteBuilder(IModelPersistence<M> caller) {
    this.caller = caller;
    this.delete = new ModelDelete();
  }

  protected void addWhereConstraint(WhereConstraint whereConstraint) {
    delete.addConstraint(whereConstraint);
  }

  protected void addIds(Collection<Long> ids) {
    delete.addIds(ids);
  }

  protected void addId(Long id) {
    delete.addId(id);
  }

  public boolean execute() throws IOException {
    return this.caller.delete(this.delete);
  }
}
