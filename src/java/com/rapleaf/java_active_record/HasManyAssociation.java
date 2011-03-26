package com.rapleaf.java_active_record;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

public class HasManyAssociation<T extends ModelWithId> implements Serializable {
  private final IModelPersistence<T> persistence;
  private final String foreignKey;
  private final int id;
  private Set<T> cachedList;

  public HasManyAssociation(IModelPersistence<T> persistence,
      String foreignKey, int id) {
    this.persistence = persistence;
    this.foreignKey = foreignKey;
    this.id = id;
  }

  public Set<T> get() throws IOException {
    if (cachedList == null) {
      cachedList = persistence.findAllByForeignKey(foreignKey, id);
    }
    return cachedList;
  }

  public void clearCache() throws IOException {
    persistence.clearCacheByForeignKey(foreignKey, id);
    cachedList = null;
  }
}
