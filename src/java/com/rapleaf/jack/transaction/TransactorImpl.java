package com.rapleaf.jack.transaction;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.ConnectionCreationFailureException;
import com.rapleaf.jack.exception.NoAvailableConnectionException;

public class TransactorImpl<DB extends IDb> implements ITransactor<DB> {
  private static final Logger LOG = LoggerFactory.getLogger(TransactorImpl.class);

  private final Callable<DB> dbConstructor;
  private final int maxConnections;
  private final Duration timeout;
  private final LinkedList<DB> allConnections;
  private final LinkedList<DB> idleConnections;

  private TransactorImpl(Callable<DB> callable, int maxConnections, Duration timeout) {
    this.dbConstructor = callable;
    this.maxConnections = maxConnections;
    this.timeout = timeout;
    this.allConnections = new LinkedList<>();
    this.idleConnections = new LinkedList<>();
  }

  public static <DB extends IDb> Builder<DB> create(Callable<DB> dbConstructor) {
    return new Builder<>(dbConstructor);
  }

  @Override
  public <T> T query(IQuery<DB, T> query) {
    DB connection = tryConnection();
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

  @Override
  public void execute(IExecution<DB> execution) {
    DB connection = tryConnection();
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

  private DB tryConnection() {
    long timeoutThreshold = System.currentTimeMillis() + timeout.getMillis();

    synchronized (idleConnections) {
      while (idleConnections.isEmpty() && allConnections.size() >= maxConnections && System.currentTimeMillis() < timeoutThreshold) {
        try {
          LOG.info("Wait for connection");
          idleConnections.wait(timeout.getMillis());
          LOG.info("Wait timed out");
        } catch (InterruptedException e) {
          throw new RuntimeException("Transaction pending for connection is interrupted ", e);
        }
      }

      if (!idleConnections.isEmpty()) {
        LOG.info("Use idle connection (total {})", idleConnections.size());
        return idleConnections.remove();
      } else if (allConnections.size() < maxConnections) {
        LOG.info("Create new connection (total {})", allConnections.size());
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

  public static class Builder<DB extends IDb> implements ITransactor.Builder<DB, TransactorImpl<DB>> {
    private Callable<DB> dbConstructor;
    private int maxConnections = 1;
    private Duration timeout = Duration.standardMinutes(1L);

    private Builder(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
    }

    public Builder<DB> setDbConstructor(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
      return this;
    }

    public Builder<DB> setMaxConnections(int maxConnections) {
      Preconditions.checkArgument(maxConnections > 0);
      this.maxConnections = maxConnections;
      return this;
    }

    public Builder<DB> setTimeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    @Override
    public TransactorImpl<DB> get() {
      return new TransactorImpl<>(dbConstructor, maxConnections, timeout);
    }
  }

}
