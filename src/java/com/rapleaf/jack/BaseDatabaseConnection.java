package com.rapleaf.jack;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseDatabaseConnection implements Serializable {

  private static Logger LOG = LoggerFactory.getLogger(BaseDatabaseConnection.class);

  protected transient Connection conn = null;
  
  /**
   * Get a Connection to a database.
   * If there is no connection, create a new one.
   */
  public abstract Connection getConnection();

  /**
   * Re-establish the connection in case it has been sitting idle for too
   * long and has been claimed by the server
   */
  public Connection resetConnection() {
    return resetConnection(null);
  }

  /**
   * Re-establish the connection in case it has been sitting idle for too
   * long and has been claimed by the server
   * This version specifies a cause and can be used when the reset is
   * performed as an attempt to recover from an exception
   */
  public Connection resetConnection(Throwable cause) {
    LOG.warn("Resetting database connection to attempt to recover from exception: {}", cause);
    if (conn != null) {
      try {
        if (!conn.getAutoCommit()) {
          throw new RuntimeException("Cannot safely reset connection. May be in the middle of a transaction.", cause);
        }
        conn.close();
      } catch (SQLException e) {
        LOG.warn("Failed to reset database connection: {}", e);
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

  public PreparedStatement getPreparedStatement(String statement, int options) {
    try {
      return getConnection().prepareStatement(statement, options);
    } catch(SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets this connection's auto-commit mode to the given state. If a connection
   * is in auto-commit mode, then all its SQL statements will be executed and
   * committed as individual transactions. Otherwise, its SQL statements are
   * grouped into transactions that are terminated by a call to either the
   * method commit or the method rollback. By default, new connections are in
   * auto-commit mode. 
   * @param autoCommit
   */
  public void setAutoCommit(boolean autoCommit) {
    try {
      getConnection().setAutoCommit(autoCommit);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Retrieves the current auto-commit mode
   */
  public boolean getAutoCommit() {
    try {
      return getConnection().getAutoCommit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Makes all changes made since the previous commit/rollback permanent and
   * releases any database locks currently held by this Connection object.
   * This method should be used only when auto-commit mode has been disabled. 
   */
  public void commit() {
    try {
      getConnection().commit();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Undoes all changes made in the current transaction and releases any
   * database locks currently held by this Connection object. This method should
   * be used only when auto-commit mode has been disabled. 
   */
  public void rollback() {
    try {
      getConnection().rollback();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
