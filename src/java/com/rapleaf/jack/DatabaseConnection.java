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
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
  private String mysqlUrl;
  private String username;
  private String password;
  private long expiresAt;
  private long expiration;

  private static final long DEFAULT_EXPIRATION = 14400000; // 4 hours

  public DatabaseConnection(String dbname_key) throws RuntimeException {
    this(dbname_key, DEFAULT_EXPIRATION);
  }
  
  public DatabaseConnection(String dbname_key, long expiration) {
    try {
      // load database info from config folder
      Map env_info = (Map)YAML.load(new FileReader("config/environment.yml"));
      String db_info_name = (String)env_info.get(dbname_key);
      Map db_info_container = (Map)YAML.load(new FileReader("config/database.yml"));
      Map<String, String> db_info = (Map<String, String>)db_info_container.get(db_info_name);

      // get mySQL credentials from database info
      String serverUrl = "jdbc:mysql://"+db_info.get("host");
      String database = db_info.get("database");
      mysqlUrl = serverUrl + "/" + database;
      username = db_info.get("username");
      password = db_info.get("password");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.expiration = expiration;
    updateExpiration();
  }

  /**
   * Get a Connection to a MySQL database.
   * If there is no connection, create a new one.
   * If the connection hasn't been used in a long time, close it and create a new one.
   * We do this because MySQL has an 8 hour idle connection timeout.
   */
  public Connection getConnection() {
    try {
      if(conn == null) {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(mysqlUrl, username, password);
      } else if (isExpired()) {
        resetConnection();
      }
      updateExpiration();
      return conn;
    } catch(Exception e) { //IOEx., SQLEx.
      throw new RuntimeException(e);
    }
  }

  private boolean isExpired() {
    return (expiresAt < System.currentTimeMillis());
  }

  private void updateExpiration() {
    expiresAt = System.currentTimeMillis() + expiration;
  }
}
