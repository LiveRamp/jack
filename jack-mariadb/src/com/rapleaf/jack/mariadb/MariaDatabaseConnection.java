package com.rapleaf.jack.mariadb;

import java.sql.Connection;
import java.sql.DriverManager;

import com.rapleaf.jack.BaseDatabaseConnection;

public class MariaDatabaseConnection extends BaseDatabaseConnection {
  private final String connectionString;

  public MariaDatabaseConnection(String connectionString) {
    this.connectionString = connectionString;
  }

  @Override
  public Connection getConnectionInternal() {
    try {
      if (conn == null) {
        conn = DriverManager.getConnection(connectionString, "root", "");
      } else if (conn.isClosed()) {
        resetConnection();
      }
      return conn;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
