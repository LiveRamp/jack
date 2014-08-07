package com.rapleaf.jack;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractModelQuery<M extends ModelWithId> implements ModelQuery<M> {

  protected Map<Enum, Object> fieldsMap;
  private IModelPersistence<M> caller;

  public AbstractModelQuery(IModelPersistence<M> caller) {
    this.caller = caller;
    this.fieldsMap = new HashMap<Enum, Object>();
  }

  public Set<M> find() throws IOException {
    return caller.find(fieldsMap);
  }
}
