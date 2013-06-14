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

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import org.jvyaml.YAML;

/**
 * The DatabaseConnection class manages connections to your databases. The
 * database to be used is specified in config/environment.yml. This file 
 * in turn points to a set of login credentials in config/database.yml.
 * 
 * All public methods methods of DatabaseConnection throw RuntimeExceptions
 * (rather than IO or SQL exceptions).
 */
public class DatabaseConnection extends BaseDatabaseConnection {
  private static final String PARTITION_NUM_ENV_VARIABLE_NAME = "TLB_PARTITION_NUMBER";

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
    Map<String, String> db_info = null;
    Map<String, Object> env_info = null;
    try {
      // load database info from config folder
      env_info = (Map<String, Object>)YAML.load(new FileReader("config/environment.yml"));
      String db_info_name = (String)env_info.get(dbname_key);
      Map db_info_container = (Map)YAML.load(new FileReader("config/database.yml"));
      db_info = (Map<String, String>)db_info_container.get(db_info_name);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // get server credentials from database info
    String adapter = db_info.get("adapter");
    String driver;
    if (adapter.equals("mysql") || adapter.equals("mysql_replication")) {
      driver = "mysql";
      driverClass = "com.mysql.jdbc.Driver";
    } else if (adapter.equals("postgresql")) {
      driver = "postgresql";
      driverClass = "org.postgresql.Driver";
    } else {
      driverClass = null;
      throw new IllegalArgumentException("Don't know the driver for adapter '" + adapter + "'!");
    }
    StringBuilder connectionStringBuilder = new StringBuilder("jdbc:");
    connectionStringBuilder.append(driver).append("://").append(db_info.get("host"));
    if (db_info.containsKey("port")) {
      connectionStringBuilder.append(":").append(Integer.parseInt(db_info.get("port")));
    }
    connectionStringBuilder.append("/").append(getDbName(db_info.get("database"), (Boolean)env_info.get("enable_parallel_tests")));
    connectionString = connectionStringBuilder.toString();
    username = db_info.get("username");
    password = db_info.get("password");

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
      if(conn == null) {
        Class.forName(driverClass);
        conn = DriverManager.getConnection(connectionString, username, password);
      } else if (isExpired()) {
        resetConnection();
      }
      updateExpiration();
      return conn;
    } catch(Exception e) { //IOEx., SQLEx.
      throw new RuntimeException(e);
    }
  }

  /**
   * When using a parallel test environment, we append an integer that lives in
   * an environment variable to the database name.
   *
   * @param base_name the name of the database
   * @param use_parallel if true, append an integer specified in an environment
   *                     variable to the end of base_name
   * @return the name of the database that we should connect to
   */
  protected String getDbName(String base_name, Boolean use_parallel) {
    if (use_parallel == null || !use_parallel) {
      return base_name;
    }
    else {
      String partitionNumber = System.getenv(PARTITION_NUM_ENV_VARIABLE_NAME);
      if (partitionNumber != null) {
        return base_name + partitionNumber;
      }
      else {
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
