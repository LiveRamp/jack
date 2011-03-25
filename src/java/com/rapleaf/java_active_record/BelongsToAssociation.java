package com.rapleaf.java_active_record;

import java.io.IOException;
import java.io.Serializable;


public class BelongsToAssociation<T extends ModelWithId<ID>, ID extends Number> implements Serializable {
  private final IModelPersistence<T, ID> persistence;
  private final ID id;
  private T cache;

  public BelongsToAssociation(IModelPersistence<T, ID> persistence, ID id) {
    this.persistence = persistence;
    this.id = id;
  }

  public T get() throws IOException {
    if (id == null) return null;
    if (cache == null) {
      cache = persistence.find(id);
    }
    return cache;
  }

  public void clearCache() throws IOException {
    persistence.clearCacheById(id);
    cache = null;
  }
}
