package com.rapleaf.jack;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModelQueryImpl<M extends ModelWithId> implements ModelQuery<M> {

  private Map<Enum, Object> fieldsMap;
  private IModelPersistence caller;

  public ModelQueryImpl(IModelPersistence caller) {
    this.caller = caller;
    this.fieldsMap = new HashMap<Enum, Object>();
  }

  public Set<M> find() throws IOException {
    return caller.find(fieldsMap);
  }
}
