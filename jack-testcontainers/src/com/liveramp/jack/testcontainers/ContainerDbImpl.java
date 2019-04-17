package com.liveramp.jack.testcontainers;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.GenericDatabases;
import com.rapleaf.jack.IDb;
import com.rapleaf.jack.transaction.TransactorImpl;

public class ContainerDbImpl<Database extends IDb> implements GenericDatabases {

  public interface DbBuilder<Database extends IDb> {
    Database getDb(BaseDatabaseConnection conn, ContainerDbImpl<Database> genericDatabases);
  }

  private String dbName;
  private Database db;

  private final DbBuilder<Database> dbBuilder;
  private final LazyLoadingSingletonFactory<MySQLContainer> dbSpecificContainer;

  public ContainerDbImpl(DbBuilder<Database> dbBuilder, LazyLoadingSingletonFactory<MySQLContainer> dbSpecificContainer, String dbName) {
    this.dbBuilder = dbBuilder;
    this.dbSpecificContainer = dbSpecificContainer;
    this.dbName = dbName;
  }

  public Database getDb() {
    if (db == null) {
      try {
        JdbcDatabaseContainer container = dbSpecificContainer.get();
        db = dbBuilder.getDb(new MysqlContainerConnection(container.getJdbcUrl()), this);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return db;
  }

  public TransactorImpl.Builder<Database> getDbTransactor() {
    return TransactorImpl.create(() -> new ContainerDbImpl<>(dbBuilder, dbSpecificContainer, dbName).getDb());
  }
}
