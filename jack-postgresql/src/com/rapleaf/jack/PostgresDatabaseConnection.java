package com.rapleaf.jack;

import static com.rapleaf.jack.DatabaseConnectionConstants.DEFAULT_EXPIRATION;
import static com.rapleaf.jack.DatabaseConnectionConstants.POSTGRESQL_JDBC_DRIVER;

public class PostgresDatabaseConnection extends DatabaseConnection {
  public PostgresDatabaseConnection(String dbname_key) {
    this(dbname_key, DEFAULT_EXPIRATION);
  }

  public PostgresDatabaseConnection(String dbname_key, long expiration) {
    super(dbname_key, expiration, POSTGRESQL_JDBC_DRIVER);
  }
}
