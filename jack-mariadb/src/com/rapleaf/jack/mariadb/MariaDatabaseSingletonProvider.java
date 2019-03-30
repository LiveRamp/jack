package com.rapleaf.jack.mariadb;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;

public class MariaDatabaseSingletonProvider extends LazyLoadingSingletonFactory<DB> {
  private final String dbName;

  public MariaDatabaseSingletonProvider(String dbName) {
    this.dbName = dbName;
  }

  @Override
  protected DB create() {
    try {
      DB database = MariaServerSingletonProvider.INSTANCE.get();

      database.createDB(dbName);
      database.source(dbName + ".dump", dbName);

      return database;
    } catch (ManagedProcessException e) {
      throw new RuntimeException(e);
    }
  }
}
