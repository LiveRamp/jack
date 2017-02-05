package com.rapleaf.jack.transaction;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import org.joda.time.Duration;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.ConnectionCreationFailureException;
import com.rapleaf.jack.exception.NoAvailableConnectionException;
import com.rapleaf.jack.exception.SqlExecutionFailureException;

abstract class AbstractDbManager<DB extends IDb> implements IDbManager<DB> {

  private final Callable<DB> dbConstructor;
  protected final int maxConnections;
  private final Duration timeout;
  protected final LinkedList<DB> allConnections;
  private final LinkedList<DB> idleConnections;

  AbstractDbManager(Callable<DB> callable, int maxConnections, Duration timeout) {
    this.dbConstructor = callable;
    this.maxConnections = maxConnections;
    this.timeout = timeout;
    this.allConnections = new LinkedList<>();
    this.idleConnections = new LinkedList<>();
  }

  @Override
  public DB getConnection(long timestamp) {
    long timeoutThreshold = timestamp + timeout.getMillis();

    synchronized (idleConnections) {
      while (idleConnections.isEmpty() && allConnections.size() >= maxConnections && System.currentTimeMillis() < timeoutThreshold) {
        try {
          idleConnections.wait(timeout.getMillis());
        } catch (InterruptedException e) {
          throw new SqlExecutionFailureException("Transaction pending for connection is interrupted ", e);
        }
      }

      if (!idleConnections.isEmpty()) {
        return idleConnections.remove();
      }

      if (!isConnectionMaximized()) {
        try {
          DB newConnection = dbConstructor.call();
          newConnection.disableCaching();
          allConnections.add(newConnection);
          return newConnection;
        } catch (Exception e) {
          throw new ConnectionCreationFailureException("DB connection creation failed", e);
        }
      }
    }

    throw new NoAvailableConnectionException("No available connection after waiting for " + timeout.getStandardSeconds() + " seconds");
  }

  @Override
  public void returnConnection(DB connection) {
    synchronized (idleConnections) {
      idleConnections.add(connection);
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

  protected abstract boolean isConnectionMaximized();

}
