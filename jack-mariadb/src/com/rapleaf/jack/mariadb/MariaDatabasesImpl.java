package com.rapleaf.jack.mariadb;

import ch.vorburger.mariadb4j.DB;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.GenericDatabases;
import com.rapleaf.jack.IDb;
import com.rapleaf.jack.transaction.TransactorImpl;

public class MariaDatabasesImpl<Database extends IDb> implements GenericDatabases {
  private String dbName;

  public interface DbBuilder<Database extends IDb> {
    Database getDb(BaseDatabaseConnection conn, MariaDatabasesImpl<Database> genericDatabases);
  }

  private Database db;

  private final DbBuilder<Database> dbBuilder;
  private final LazyLoadingSingletonFactory<DB> dbSpecificMariaProvider;

  public MariaDatabasesImpl(DbBuilder<Database> dbBuilder, LazyLoadingSingletonFactory<DB> dbSpecificMariaProvider, String dbName) {
    this.dbBuilder = dbBuilder;
    this.dbSpecificMariaProvider = dbSpecificMariaProvider;
    this.dbName = dbName;
  }

  public Database getDb() {
    if (db == null) {
      try {
        DB database = dbSpecificMariaProvider.get();
        db = dbBuilder.getDb(new MariaDatabaseConnection(database.getConfiguration().getURL(dbName)), this);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return db;
  }

  public TransactorImpl.Builder<Database> getDbTransactor() {
    return TransactorImpl.create(() -> new MariaDatabasesImpl<>(dbBuilder, dbSpecificMariaProvider, dbName).getDb());
  }
}
