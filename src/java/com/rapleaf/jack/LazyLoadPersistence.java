package com.rapleaf.jack;

public abstract class LazyLoadPersistence<T extends IModelPersistence, D extends GenericDatabases> {

  private final BaseDatabaseConnection conn;
  private final D databases;

  private volatile T persistence;

  private boolean disableCaching;

  public LazyLoadPersistence(BaseDatabaseConnection conn, D databases) {
    this.conn = conn;
    this.databases = databases;

    this.persistence = null;
    this.disableCaching = false;
  }

  public T get() {
    if (persistence == null) {
      synchronized (this) {
        this.persistence = build(conn, databases);
      }
    }

    if (disableCaching) {
      persistence.disableCaching();
    }

    return persistence;
  }

  public void disableCaching() {
    disableCaching = true;
  }

  protected abstract T build(BaseDatabaseConnection conn, D databases);
}
