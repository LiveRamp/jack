package com.rapleaf.jack;

import java.sql.PreparedStatement;

import static com.rapleaf.jack.DatabaseConnectionConstants.DEFAULT_EXPIRATION;
import static com.rapleaf.jack.DatabaseConnectionConstants.REDSHIFT_JDBC_DRIVER;

public class RedshiftDatabaseConnection extends DatabaseConnection {
  public RedshiftDatabaseConnection(String dbname_key) {
    this(dbname_key, DEFAULT_EXPIRATION);
  }

  public RedshiftDatabaseConnection(String dbname_key, long expiration) {
    super(dbname_key, expiration, REDSHIFT_JDBC_DRIVER);
  }

  @Override
  public PreparedStatement getPreparedStatement(String statement) {
    if (driverClass.equals(REDSHIFT_JDBC_DRIVER)) {
      statement = statement.replaceAll("`", "\"");
    }
    return super.getPreparedStatement(statement);
  }

  @Override
  public PreparedStatement getPreparedStatement(String statement, int options) {
    if (driverClass.equals(REDSHIFT_JDBC_DRIVER)) {
      statement = statement.replaceAll("`", "\"");
    }
    return super.getPreparedStatement(statement, options);
  }
}
