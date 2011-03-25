package com.rapleaf.java_active_record;

import java.io.IOException;
import java.io.Serializable;

public class HasOneAssociation<T extends ModelWithId, ID extends Number> implements Serializable {
  private final IModelPersistence<T, ?> persistence;
  private final String foreignKey;
  private final ID id;
  private T cached;

  public HasOneAssociation(IModelPersistence<T, ?> persistence,
      String foreignKey, ID id) {
    this.persistence = persistence;
    this.foreignKey = foreignKey;
    this.id = id;
  }

  public T get() throws IOException {
    if (id == null) return null;
    if (cached == null) {
      cached = (T) persistence.findAllByForeignKey(foreignKey, id.longValue()).toArray()[0];
    }
    return cached;
  }

  public void clearCache() throws IOException {
    persistence.clearCacheByForeignKey(foreignKey, id.longValue());
    cached = null;
  }
}
