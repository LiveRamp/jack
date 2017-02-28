package com.rapleaf.jack.transaction;

import java.util.concurrent.Callable;

import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.SqlExecutionFailureException;

public class TransactorImpl<DB extends IDb> implements ITransactor<DB> {
  private static final Logger LOG = LoggerFactory.getLogger(TransactorImpl.class);

  private static int DEFAULT_MAX_TOTAL_CONNECTIONS = 1;
  private static int DEFAULT_MIN_IDLE_CONNECTIONS = 1;
  private static Duration DEFAULT_MAX_WAIT_TIME = Duration.standardSeconds(30);
  private static Duration DEFAULT_KEEP_ALIVE_TIME = Duration.standardMinutes(30);

  private final IDbManager<DB> dbManager;

  TransactorImpl(IDbManager<DB> dbManager) {
    this.dbManager = dbManager;
  }

  public static <DB extends IDb> Builder<DB> create(Callable<DB> dbConstructor) {
    return new Builder<>(dbConstructor);
  }

  @Override
  public <T> T execute(IQuery<DB, T> query) {
    return execute(query, false);
  }

  @Override
  public <T> T executeAsTransaction(IQuery<DB, T> query) {
    return execute(query, true);
  }

  @Override
  public void execute(IExecution<DB> execution) {
    execute(execution, false);
  }

  @Override
  public void executeAsTransaction(IExecution<DB> execution) {
    execute(execution, true);
  }

  private <T> T execute(IQuery<DB, T> query, boolean asTransaction) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    connection.setAutoCommit(!asTransaction);
    try {
      T value = query.query(connection);
      if (asTransaction) {
        connection.commit();
      }
      return value;
    } catch (Exception e) {
      LOG.error("SQL execution failure", e);
      if (asTransaction) {
        connection.rollback();
      }
      throw new SqlExecutionFailureException(e);
    } finally {
      dbManager.returnConnection(connection);
    }
  }

  private void execute(IExecution<DB> execution, boolean asTransaction) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    connection.setAutoCommit(!asTransaction);
    try {
      execution.execute(connection);
      if (asTransaction) {
        connection.commit();
      }
    } catch (Exception e) {
      LOG.error("SQL execution failure", e);
      if (asTransaction) {
        connection.rollback();
      }
      throw new SqlExecutionFailureException(e);
    } finally {
      dbManager.returnConnection(connection);
    }
  }

  @Override
  public void close() {
    dbManager.close();
  }

  public static class Builder<DB extends IDb> implements ITransactor.Builder<DB, TransactorImpl<DB>> {

    private final Callable<DB> dbConstructor;
    private int maxTotalConnections = DEFAULT_MAX_TOTAL_CONNECTIONS;
    private int minIdleConnections = DEFAULT_MIN_IDLE_CONNECTIONS;
    private long maxWaitMillis = DEFAULT_MAX_WAIT_TIME.getMillis();
    private long keepAliveMillis = DEFAULT_KEEP_ALIVE_TIME.getMillis();

    Builder(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
    }

    public Builder<DB> setMaxTotalConnections(int maxTotalConnections) {
      this.maxTotalConnections = maxTotalConnections;
      return this;
    }

    public Builder<DB> setMinIdleConnections(int minIdleConnections) {
      this.minIdleConnections = minIdleConnections;
      return this;
    }

    public Builder<DB> setMaxWaitTime(Duration maxWaitTime) {
      this.maxWaitMillis = maxWaitTime.getMillis();
      return this;
    }

    public Builder<DB> setKeepAliveTime(Duration keepAliveTime) {
      this.keepAliveMillis = keepAliveTime.getMillis();
      return this;
    }

    public Builder<DB> enableInfiniteWait() {
      this.maxWaitMillis = -1L;
      return this;
    }

    @Override
    public TransactorImpl<DB> get() {
      return Builder.build(this);
    }

    private static <DB extends IDb> TransactorImpl<DB> build(Builder<DB> builder) {
      DbPoolManager<DB> dbPoolManager = new DbPoolManager<DB>(builder.dbConstructor, builder.maxTotalConnections,
          builder.minIdleConnections, builder.maxWaitMillis, builder.keepAliveMillis);
      return new TransactorImpl<DB>(dbPoolManager);
    }
  }

}
