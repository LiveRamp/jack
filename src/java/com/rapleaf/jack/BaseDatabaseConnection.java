package com.rapleaf.jack;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseDatabaseConnection implements Serializable, Closeable {

  private static Logger LOG = LoggerFactory.getLogger(BaseDatabaseConnection.class);

  public static String DISABLE_PROPERTY = "jack.db.disallow_connections";
  public static String DISABLE_ENV_VAR = "JACK_DB_DISALLOW_CONNECTIONS";


  protected transient Connection conn = null;

  /**
   * Get a Connection to a database.
   * If there is no connection, create a new one.
   */
  public Connection getConnection() {
    if (!isDisabled()) {
      return getConnectionInternal();
    } else {
      throw new RuntimeException("Tried to instantiate a connection even though connections have been disabled in this environment");
    }
  }

  protected abstract Connection getConnectionInternal();


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
    LOG.warn("Resetting database connection to attempt to recover from exception:", cause);
    if (conn != null) {
      try {
        if (!conn.getAutoCommit()) {
          conn = null;
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
    } else {
      return false;
    }
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
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public PreparedStatement getPreparedStatement(String statement, int options) {
    try {
      return getConnection().prepareStatement(statement, options);
    } catch (SQLException e) {
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
   *
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

  /**
   * Close the connection to the database.
   */
  @Override
  public void close() throws IOException {
    try {
      if (conn != null) {
        getConnection().close();
        conn = null;
      }
    } catch (SQLException e) {
      throw new IOException(e);
    }
  }

  private boolean isDisabled() {
    try {
      Boolean disabledBySystemProperty = false;
      try {
        disabledBySystemProperty = Boolean.parseBoolean(System.getProperty(DISABLE_PROPERTY));
      } catch (IllegalArgumentException e) {
        //property is not set
      }
      Boolean disabledByEnvVar = Boolean.parseBoolean(System.getenv(DISABLE_ENV_VAR));

      return disabledBySystemProperty || disabledByEnvVar;
    } catch (Exception e) {
      LOG.error("Encountered an error trying to determine disabled status: ", e); //migration safety code TODO remove
      return false;
    }
  }
}
