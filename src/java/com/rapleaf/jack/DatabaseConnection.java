//
// Copyright 2011 Rapleaf
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.rapleaf.jack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * The DatabaseConnection class manages connections to your databases. The
 * database to be used is specified in config/environment.yml. This file
 * in turn points to a set of login credentials in config/database.yml.
 * <p/>
 * All public methods methods of DatabaseConnection throw RuntimeExceptions
 * (rather than IO or SQL exceptions).
 */
public class DatabaseConnection extends BaseDatabaseConnection {
  private static final String PARTITION_NUM_ENV_VARIABLE_NAME = "TLB_PARTITION_NUMBER";

  public static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
  public static final String POSTGRESQL_JDBC_DRIVER = "org.postgresql.Driver";
  public static final String REDSHIFT_JDBC_DRIVER = "com.amazon.redshift.jdbc41.Driver";

  private final String connectionString;
  private final String username;
  private final String password;
  private final String driverClass;
  private long expiresAt;
  private long expiration;

  private static final long DEFAULT_EXPIRATION = 14400000; // 4 hours

  public DatabaseConnection(String dbname_key) throws RuntimeException {
    this(dbname_key, DEFAULT_EXPIRATION);
  }

  public DatabaseConnection(String dbname_key, long expiration) {

    DatabaseConnectionConfiguration config = DatabaseConnectionConfiguration.loadFromEnvironment(dbname_key);

    // get server credentials from database info
    String adapter = config.getAdapter();
    String driver;
    if (adapter.equals("mysql") || adapter.equals("mysql_replication") || adapter.equals("mysql2")) {
      driver = "mysql";
      driverClass = MYSQL_JDBC_DRIVER;
    } else if (adapter.equals("postgresql")) {
      driver = "postgresql";
      driverClass = POSTGRESQL_JDBC_DRIVER;
    } else if (adapter.equals("redshift")) {
      driver = "redshift";
      driverClass = REDSHIFT_JDBC_DRIVER;
    } else {
      driverClass = null;
      throw new IllegalArgumentException("Don't know the driver for adapter '" + adapter + "'!");
    }
    StringBuilder connectionStringBuilder = new StringBuilder("jdbc:");
    connectionStringBuilder.append(driver).append("://").append(config.getHost());
    if (config.getPort().isPresent()) {
      connectionStringBuilder.append(":").append(config.getPort().get());
    }
    connectionStringBuilder.append("/").append(getDbName(config.getDatabaseName(), config.enableParallelTests()));
    connectionString = connectionStringBuilder.toString();
    username = config.getUsername();
    password = config.getPassword();

    this.expiration = expiration;
    updateExpiration();
  }

  /**
   * Get a Connection to a database. If there is no connection, create a new one.
   * If the connection hasn't been used in a long time, close it and create a new one.
   * We do this because MySQL has an 8 hour idle connection timeout.
   */
  public Connection getConnection() {
    try {
      if (conn == null) {
        Class.forName(driverClass);
        conn = DriverManager.getConnection(connectionString, username, password);
      } else if (isExpired() || conn.isClosed()) {
        resetConnection();
      }
      updateExpiration();
      return conn;
    } catch (Exception e) { //IOEx., SQLEx.
      throw new RuntimeException(e);
    }
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

  /**
   * When using a parallel test environment, we append an integer that lives in
   * an environment variable to the database name.
   *
   * @param base_name    the name of the database
   * @param use_parallel if true, append an integer specified in an environment
   *                     variable to the end of base_name
   * @return the name of the database that we should connect to
   */
  protected String getDbName(String base_name, Boolean use_parallel) {
    if (use_parallel == null || !use_parallel) {
      return base_name;
    } else {
      String partitionNumber = System.getenv(PARTITION_NUM_ENV_VARIABLE_NAME);
      if (partitionNumber != null) {
        return base_name + partitionNumber;
      } else {
        throw new RuntimeException("Expected the " + PARTITION_NUM_ENV_VARIABLE_NAME + " environment variable to be set, but it wasn't. Either disable parallel tests or make sure the variable is defined.");
      }
    }
  }

  private boolean isExpired() {
    return (expiresAt < System.currentTimeMillis());
  }

  private void updateExpiration() {
    expiresAt = System.currentTimeMillis() + expiration;
  }
}
