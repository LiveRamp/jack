package com.rapleaf.jack;

public class PostgresDatabaseConnection extends DatabaseConnection {
  public PostgresDatabaseConnection(String dbname_key) {
    this(dbname_key, DEFAULT_EXPIRATION);
  }

  public PostgresDatabaseConnection(String dbname_key, long expiration) {
    super(dbname_key, expiration, POSTGRESQL_JDBC_DRIVER);
  }
}
