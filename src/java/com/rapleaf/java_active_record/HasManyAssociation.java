package com.rapleaf.java_active_record;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

public class HasManyAssociation<T extends ModelWithId, ID extends Number> implements Serializable {
  private final IModelPersistence<T, ?> persistence;
  private final String foreignKey;
  private final ID id;
  private Set<T> cachedList;

  public HasManyAssociation(IModelPersistence<T, ?> persistence,
      String foreignKey, ID id) {
    this.persistence = persistence;
    this.foreignKey = foreignKey;
    this.id = id;
  }

  public Set<T> get() throws IOException {
    if (id == null) return null;
    if (cachedList == null) {
      cachedList = persistence.findAllByForeignKey(foreignKey, id.longValue());
    }
    return cachedList;
  }

  public void clearCache() throws IOException {
    persistence.clearCacheByForeignKey(foreignKey, id.longValue());
    cachedList = null;
  }
}
