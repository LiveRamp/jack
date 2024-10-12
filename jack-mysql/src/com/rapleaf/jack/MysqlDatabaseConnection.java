package com.rapleaf.jack;

import static com.rapleaf.jack.DatabaseConnectionConstants.DEFAULT_EXPIRATION;
import static com.rapleaf.jack.DatabaseConnectionConstants.MYSQL_JDBC_DRIVER;

import java.util.HashMap;
import java.util.Map;

public class MysqlDatabaseConnection extends DatabaseConnection {

  private static final Map<String, String> sslOptions = new HashMap<>();

  static {
    sslOptions.put("verifyServerCertificate", "false");
    sslOptions.put("useSSL", "true");
  }

  public MysqlDatabaseConnection(String dbname_key) {
    this(dbname_key, DEFAULT_EXPIRATION);
  }

  public MysqlDatabaseConnection(String dbname_key, long expiration) {
    super(dbname_key, expiration, MYSQL_JDBC_DRIVER, sslOptions);
  }

  public MysqlDatabaseConnection(String dbname_key, long expiration, Map<String, String> additionalOptions) {
    super(dbname_key, expiration, MYSQL_JDBC_DRIVER, additionalOptions);
  }
}
