package com.rapleaf.jack;

import java.io.Serializable;

public class LazyLoadPersistence<T extends IModelPersistence, D extends GenericDatabases> implements Serializable {
  @FunctionalInterface
  public interface Factory<T extends IModelPersistence<?, ?>, D extends GenericDatabases> {
    T create(BaseDatabaseConnection conn, D databases);
  }

  private final BaseDatabaseConnection conn;
  private final D databases;
  private final Factory<T, D> factory;

  private volatile T persistence;

  private volatile boolean disableCaching;

  public LazyLoadPersistence(BaseDatabaseConnection conn, D databases, Factory<T, D> factory) {
    this.conn = conn;
    this.databases = databases;

    this.persistence = null;
    this.disableCaching = false;
    this.factory = factory;
  }

  public T get() {
    if (persistence == null) {
      synchronized (this) {
        if (persistence == null) {
          this.persistence = factory.create(conn, databases);

          if (disableCaching) {
            persistence.disableCaching();
          }
        }
      }
    }

    return persistence;
  }

  public void disableCaching() {
    disableCaching = true;

    if (persistence != null) {
      persistence.disableCaching();
    }
  }
}
