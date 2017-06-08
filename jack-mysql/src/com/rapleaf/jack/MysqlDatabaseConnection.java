package com.rapleaf.jack;

import static com.rapleaf.jack.DatabaseConnectionConstants.DEFAULT_EXPIRATION;
import static com.rapleaf.jack.DatabaseConnectionConstants.MYSQL_JDBC_DRIVER;

public class MysqlDatabaseConnection extends DatabaseConnection {
  public MysqlDatabaseConnection(String dbname_key) {
    this(dbname_key, DEFAULT_EXPIRATION);
  }

  public MysqlDatabaseConnection(String dbname_key, long expiration) {
    super(dbname_key, expiration, MYSQL_JDBC_DRIVER);
  }
}
