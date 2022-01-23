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

import static com.rapleaf.jack.DatabaseConnectionConstants.MYSQL_JDBC_DRIVER;
import static com.rapleaf.jack.DatabaseConnectionConstants.POSTGRESQL_JDBC_DRIVER;
import static com.rapleaf.jack.DatabaseConnectionConstants.REDSHIFT_JDBC_DRIVER;

import com.google.common.base.Optional;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The DatabaseConnection class manages connections to your databases. The database to be used is specified in
 * config/environment.yml. This file in turn points to a set of login credentials in config/database.yml.
 * <p/>
 * All public methods methods of DatabaseConnection throw RuntimeExceptions (rather than IO or SQL exceptions).
 */
public class DatabaseConnection extends BaseDatabaseConnection {

  private static final String PARTITION_NUM_ENV_VARIABLE_NAME = "TLB_PARTITION_NUMBER";
  private static final int DEFAULT_CONNECTION_MAX_RETRIES = 7;
  private static final int DEFAULT_VALIDATION_TIMEOUT_SECONDS = 3;

  static final Map VALID_ADAPTER_CONSTANTS = Collections.unmodifiableMap(new HashMap() {
    private static final long serialVersionUID = 1L;

    {
      put("mysql", MYSQL_JDBC_DRIVER);
      put("mysql2", MYSQL_JDBC_DRIVER);
      put("mysql_replication", MYSQL_JDBC_DRIVER);
      put("postgresql", POSTGRESQL_JDBC_DRIVER);
      put("redshift", REDSHIFT_JDBC_DRIVER);
    }
  });

  static final Map<String, String> ADAPTER_TO_DRIVER = Collections.unmodifiableMap(new HashMap() {
    private static final long serialVersionUID = 1L;

    {
      put("mysql2", "mysql");
      put("mysql_replication", "mysql");
    }
  });


  private final String connectionString;
  private final Optional<String> username;
  private final Optional<String> password;
  private final int connectionMaxRetries;
  private final int validationTimeoutSeconds;

  protected String driverClass;
  private long expiresAt;
  private long expiration;

  public DatabaseConnection(String dbname_key, long expiration, String driverClass) {
    this(dbname_key, expiration, driverClass, Collections.emptyMap());
  }

  public DatabaseConnection(
      String dbname_key,
      long expiration,
      String driverClass,
      Map<String, String> additionalConnectionStringOptions
  ) {
    DatabaseConnectionConfiguration config = DatabaseConnectionConfiguration.loadFromEnvironment(dbname_key);
    // get server credentials from database info
    String adapter = config.getAdapter();
    if (!VALID_ADAPTER_CONSTANTS.get(adapter).equals(driverClass)) {
      throw new IllegalArgumentException("Don't know the driver for adapter '" + adapter + "'!");
    }
    this.driverClass = driverClass;

    String driver = adapter;
    if (ADAPTER_TO_DRIVER.containsKey(adapter)) {
      driver = ADAPTER_TO_DRIVER.get(adapter);
    }

    StringBuilder connectionStringBuilder = new StringBuilder("jdbc:");
    connectionStringBuilder.append(driver).append("://").append(config.getHost());
    if (config.getPort().isPresent()) {
      connectionStringBuilder.append(":").append(config.getPort().get());
    }
    connectionStringBuilder.append("/").append(getDbName(config.getDatabaseName(), config.enableParallelTests()));
    if (!additionalConnectionStringOptions.isEmpty()) {
      connectionStringBuilder.append("?");
      connectionStringBuilder.append(
          additionalConnectionStringOptions.entrySet()
              .stream()
              .map(e -> String.join("=", e.getKey(), e.getValue()))
              .collect(Collectors.joining("&"))
      );
    }
    connectionString = connectionStringBuilder.toString();
    username = config.getUsername();
    password = config.getPassword();

    connectionMaxRetries = config.getConnectionMaxRetries()
        .or(DEFAULT_CONNECTION_MAX_RETRIES);
    validationTimeoutSeconds = config.getConnectionValidationTimeout()
        .or(DEFAULT_VALIDATION_TIMEOUT_SECONDS);

    this.expiration = expiration;
    updateExpiration();
  }

  /**
   * Get a Connection to a database. If there is no connection, create a new one. If the connection hasn't been used in
   * a long time, close it and create a new one. We do this because MySQL has an 8 hour idle connection timeout.
   * <p>
   * Because of the intermittent downtime of SQL service, it implements exponential retry policy. The default retry
   * policy retries seven times, which handles SQL downtime less than approx. two minutes.
   */

  public Connection getConnectionInternal() {
    for (int retryCount = 0; retryCount < connectionMaxRetries; ++retryCount) {
      try {
        if (conn == null) {
          Class.forName(driverClass);
          conn = DriverManager.getConnection(connectionString, username.orNull(), password.orNull());
        } else if (isExpired() || !conn.isValid(validationTimeoutSeconds)) {
          resetConnection();
        }
        updateExpiration();
        return conn;
      } catch (Exception e) { //IOEx., SQLEx.
        // if it is the last retry, throw exception
        if (retryCount == connectionMaxRetries - 1) {
          throw new RuntimeException(String.format(
              "Could not establish connection after %d attempts",
              connectionMaxRetries
          ), e);
        }
        // Wait (2^retryCount) seconds
        try {
          Thread.sleep(Math.round(Math.pow(2, retryCount)) * 1000);
        } catch (Exception ignored) {
          // Catches interruptions in the sleep
        }
      }
    }
    throw new RuntimeException(String.format(
        "Could not establish connection after %d attempts",
        connectionMaxRetries
    ));
  }


  /**
   * When using a parallel test environment, we append an integer that lives in an environment variable to the database
   * name.
   *
   * @param base_name    the name of the database
   * @param use_parallel if true, append an integer specified in an environment variable to the end of base_name
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
        throw new RuntimeException("Expected the " + PARTITION_NUM_ENV_VARIABLE_NAME + " environment variable to be " +
            "set, but it wasn't. Either disable parallel tests or make sure the variable is defined.");
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
