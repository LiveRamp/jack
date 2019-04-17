package com.liveramp.jack.testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;

import com.rapleaf.jack.BaseDatabaseConnection;

public class MysqlContainerConnection extends BaseDatabaseConnection {

  private final String connectionString;

  public MysqlContainerConnection(String connectionString) {
    this.connectionString = connectionString;
  }

  @Override
  public Connection getConnectionInternal() {
    try {
      if (conn == null) {
        conn = DriverManager.getConnection(connectionString, MysqlContainerConstants.USERNAME, MysqlContainerConstants.PASSWORD);
      } else if (conn.isClosed()) {
        resetConnection();
      }
      return conn;
    } catch (Exception e) { //IOEx., SQLEx.
      throw new RuntimeException(e);
    }
  }
}
