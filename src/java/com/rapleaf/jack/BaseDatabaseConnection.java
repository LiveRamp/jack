package com.rapleaf.jack;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseDatabaseConnection implements Serializable {

  transient Connection conn = null;
  
  /**
   * Get a Connection to a MySQL database.
   * If there is no connection, create a new one.
   */
  public abstract Connection getConnection();

  /**
   * Re-establish the connection in case it has been sitting idle for too 
   * long and has been claimed by the server
   */
  public Connection resetConnection() {
    if (conn != null) {
      try {
        conn.close();
      } catch (Exception e) {
        // do nothing
      }
    }
    conn = null;
    return getConnection();
  }

  /**
   * Creates a connection using the argument credentials. Useful for when 
   * MapReduce workers machines need to make database connections, as they 
   * don't have access to the local config file. Returns true if the new 
   * connection is made and false if a connection already exists.
   */
  public boolean connect() {
    if (conn == null) {
      conn = getConnection();
      return true;
    } else
      return false;
  }

  /**
   * Creates a Statement object that can be used to send SQL queries to the RapLeaf 
   * database.
   */
  public Statement getStatement() {
    try {
      return getConnection().createStatement();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a PreparedStatement object that can be used to send SQL queries to the 
   * RapLeaf database.
   */
  public PreparedStatement getPreparedStatement(String statement) {
    try {
      return getConnection().prepareStatement(statement);
    } catch(SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
