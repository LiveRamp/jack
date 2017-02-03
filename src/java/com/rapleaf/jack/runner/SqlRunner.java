package com.rapleaf.jack.runner;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;
import org.joda.time.Duration;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.ConnectionCreationFailureException;

public class SqlRunner<DB extends IDb> implements Closeable {

  private final Callable<DB> dbConstructor;
  private final int maxConnections;
  private final Duration timeout;
  private final LinkedList<DB> allConnections;
  private final LinkedList<DB> idleConnections;

  private SqlRunner(Callable<DB> callable, int maxConnections, Duration timeout) {
    this.dbConstructor = callable;
    this.maxConnections = maxConnections;
    this.timeout = timeout;
    this.allConnections = new LinkedList<>();
    this.idleConnections = new LinkedList<>();
  }

  public static <DB extends IDb> Builder<DB> create(Callable<DB> dbConstructor) {
    return new Builder<>(dbConstructor);
  }

  public <T> T query(IQuery<DB, T> query) throws ConnectionCreationFailureException {
    DB connection = getConnection();
    try {
      T value = query.query(connection);
      connection.commit();
      return value;
    } catch (IOException e) {
      connection.rollback();
      throw new RuntimeException(e);
    } finally {
      returnConnection(connection);
    }
  }

  public void execute(IExecution<DB> execution) throws ConnectionCreationFailureException {
    DB connection = getConnection();
    try {
      execution.execute(connection);
      connection.commit();
    } catch (IOException e) {
      connection.rollback();
      throw new RuntimeException(e);
    } finally {
      returnConnection(connection);
    }
  }

  private DB getConnection() throws ConnectionCreationFailureException {
    long timeoutThreshold = System.currentTimeMillis() + timeout.getMillis();

    synchronized (idleConnections) {
      while (idleConnections.isEmpty() && allConnections.size() >= maxConnections && System.currentTimeMillis() < timeoutThreshold) {
        try {
          wait(timeout.getMillis());
        } catch (InterruptedException e) {
          throw new ConnectionCreationFailureException("Transaction pending interrupted ", e);
        }
      }

      if (!idleConnections.isEmpty()) {
        return idleConnections.remove();
      } else if (allConnections.size() < maxConnections) {
        try {
          DB newConnection = dbConstructor.call();
          newConnection.setAutoCommit(false);
          newConnection.disableCaching();
          allConnections.add(newConnection);
          return newConnection;
        } catch (Exception e) {
          throw new ConnectionCreationFailureException("DB construction failed", e);
        }
      }
    }

    throw new ConnectionCreationFailureException("Transaction creation timeout after " + timeout.getStandardSeconds() + " seconds");
  }

  private void returnConnection(DB dbConnection) {
    synchronized (idleConnections) {
      idleConnections.add(dbConnection);
      idleConnections.notify();
    }
  }

  @Override
  public void close() throws IOException {
    synchronized (idleConnections) {
      for (DB connection : allConnections) {
        connection.close();
      }
    }
  }

  public static class Builder<DB extends IDb> {
    private final Callable<DB> dbConstructor;
    private int maxConnections = 1;
    private Duration timeout = Duration.standardMinutes(1L);

    private Builder(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
    }

    public Builder setMaxConnections(int maxConnections) {
      Preconditions.checkArgument(maxConnections > 0);
      this.maxConnections = maxConnections;
      return this;
    }

    public Builder setTimeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public SqlRunner<DB> get() {
      return new SqlRunner<>(dbConstructor, maxConnections, timeout);
    }
  }

}
