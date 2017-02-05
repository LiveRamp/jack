package com.rapleaf.jack.transaction;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.ConnectionCreationFailureException;
import com.rapleaf.jack.exception.NoAvailableConnectionException;
import com.rapleaf.jack.exception.TransactionFailureException;

abstract class AbstractDbManager<DB extends IDb> implements IDbManager<DB> {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractDbManager.class);

  private final Callable<DB> dbConstructor;
  private final Duration timeout;
  protected final LinkedList<DB> allConnections;
  private final LinkedList<DB> idleConnections;

  AbstractDbManager(Callable<DB> callable, Duration timeout) {
    this.dbConstructor = callable;
    this.timeout = timeout;
    this.allConnections = new LinkedList<>();
    this.idleConnections = new LinkedList<>();
  }

  @Override
  public DB getConnection(long timestamp) {
    long timeoutThreshold = timestamp + timeout.getMillis();

    synchronized (idleConnections) {
      while (idleConnections.isEmpty() && isConnectionMaximized() && System.currentTimeMillis() < timeoutThreshold) {
        try {
          LOG.info("Wait for connection");
          System.out.printf("[%d] Wait for connection\n", timestamp);
          idleConnections.wait(timeout.getMillis());
          LOG.info("Wait timed out");
          System.out.printf("[%d] Wait timed out\n", timestamp);
        } catch (InterruptedException e) {
          throw new TransactionFailureException("Transaction pending for connection is interrupted ", e);
        }
      }

      if (!idleConnections.isEmpty()) {
        LOG.info("Use idle connection (total {})", idleConnections.size());
        System.out.printf("[%d] Use idle connection (total %d)\n", timestamp, idleConnections.size());
        return idleConnections.remove();
      }

      if (!isConnectionMaximized()) {
        LOG.info("Create new connection (current {})", allConnections.size());
        System.out.printf("[%d] Create new connection (current %d)\n", timestamp, allConnections.size());
        try {
          DB newConnection = dbConstructor.call();
          newConnection.setAutoCommit(false);
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
      System.out.printf("Current connections %d\n", idleConnections.size());
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
