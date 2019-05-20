package com.rapleaf.jack.transaction;

import com.google.common.base.Preconditions;
import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.SqlExecutionFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * If there is any exception while executing the query, throws {@link com.rapleaf.jack.exception.SqlExecutionFailureException}.
 * <p>
 * If there is no available connections, throws {@link com.rapleaf.jack.exception.NoAvailableConnectionException}.
 * Users can either increase the max total connections or max wait time.
 * <p>
 * If the DB manager has already been closed, throws {@link IllegalStateException}.
 * <p>
 * If new DB connections cannot be created, throws {@link com.rapleaf.jack.exception.ConnectionCreationFailureException}.
 */
public class TransactorImpl<DB extends IDb> implements ITransactor<DB> {

  private static final Logger LOG = LoggerFactory.getLogger(TransactorImpl.class);

  private final IDbManager<DB> dbManager;

  private static final int QUERY_LOG_SIZE = 30;

  private TransactorMetricsImpl queryMetrics = new TransactorMetricsImpl(QUERY_LOG_SIZE);

  private boolean metricsTrackingEnabled = DbPoolManager.DEFAULT_METRICS_TRACKING_ENABLED;

  TransactorImpl(IDbManager<DB> dbManager, boolean metricsTrackingEnabled) {
    this.dbManager = dbManager;
    this.metricsTrackingEnabled = metricsTrackingEnabled;
  }

  public static <DB extends IDb> Builder<DB> create(Callable<DB> dbConstructor) {
    return new Builder<>(dbConstructor);
  }

  @Override
  public <T> T query(IQuery<DB, T> query) {
    return query(query, false);
  }

  @Override
  public <T> T queryAsTransaction(IQuery<DB, T> query) {
    return query(query, true);
  }

  @Override
  public void execute(IExecution<DB> execution) {
    execute(execution, false);
  }

  @Override
  public void executeAsTransaction(IExecution<DB> execution) {
    execute(execution, true);
  }

  TransactorMetrics getQueryMetrics() {
    if (!metricsTrackingEnabled) {
      return new MockTransactorMetrics();
    } else {
      return queryMetrics;
    }
  }

  DbMetrics getDbMetrics() {
    return dbManager.getMetrics();
  }

  private <T> T query(IQuery<DB, T> query, boolean asTransaction) {
    DB connection = dbManager.getConnection();
    try {
      connection.setAutoCommit(!asTransaction);
      long startTime = System.currentTimeMillis();
      T value = query.query(connection);
      if (asTransaction) {
        connection.commit();
      }
      long executionTime = System.currentTimeMillis() - startTime;
      if (metricsTrackingEnabled) {
        queryMetrics.update(executionTime, Thread.currentThread().getStackTrace()[3]);
      }

      // We don't return the connection in a finally block because we don't want to
      // do so in the case that a Throwable is thrown. In that case, the JVM is almost certainly
      // about to exit, so returning the connection isn't important and in some cases can actually
      // cause harm like committing a half-complete transaction (part of returning the connection is setting
      // its autocommit property to true, which would commit a half-complete transaction).
      dbManager.returnConnection(connection);
      return value;
    } catch (Exception e) {
      LOG.error("SQL execution failure", e);
      if (asTransaction) {
        connection.rollback();
      }

      dbManager.returnConnection(connection);
      throw new SqlExecutionFailureException(e);
    } catch (Throwable t) {

      // We still try to explicitly rollback the transaction if a throwable is thrown.
      if (asTransaction) {
        connection.rollback();
      }

      throw t;
    }
  }

  private void execute(IExecution<DB> execution, boolean asTransaction) {
    query(db -> {
      execution.execute(db);
      return Optional.empty();
    });
  }

  @Override
  public void close() {
    if (metricsTrackingEnabled) {
      LOG.info("{}\n\n{}", dbManager.getMetrics().getSummary(), getQueryMetrics().getSummary());
    }
    dbManager.close();
  }

  @Override
  public DbPoolStatus getDbPoolStatus() {
    return this.dbManager.getDbPoolStatus();
  }

  public static class Builder<DB extends IDb> implements ITransactor.Builder<DB, TransactorImpl<DB>> {

    final Callable<DB> dbConstructor;
    int maxTotalConnections = DbPoolManager.DEFAULT_MAX_TOTAL_CONNECTIONS;
    int minIdleConnections = DbPoolManager.DEFAULT_MIN_IDLE_CONNECTIONS;
    long maxWaitMillis = DbPoolManager.DEFAULT_MAX_WAIT_TIME;
    long keepAliveMillis = DbPoolManager.DEFAULT_KEEP_ALIVE_TIME;
    boolean metricsTrackingEnabled = DbPoolManager.DEFAULT_METRICS_TRACKING_ENABLED;

    Builder(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
    }

    /**
     * @param maxTotalConnections The maximum number of connections that can be created in the pool. Negative values
     *                            mean infinite number of connections are allowed.
     * @throws IllegalArgumentException if the value is zero.
     */
    public Builder<DB> setMaxTotalConnections(int maxTotalConnections) {
      Preconditions.checkArgument(maxTotalConnections != 0, "Max total connections cannot be zero");
      this.maxTotalConnections = maxTotalConnections;
      return this;
    }

    /**
     * Allow infinite number of connections.
     */
    public Builder<DB> enableInfiniteConnections() {
      this.maxTotalConnections = -1;
      return this;
    }

    /**
     * @param minIdleConnections The minimum number of idle connections to keep in the pool. Zero or negative values
     *                           will be ignored.
     */
    public Builder<DB> setMinIdleConnections(int minIdleConnections) {
      this.minIdleConnections = minIdleConnections;
      return this;
    }

    /**
     * @param maxWaitTime The maximum amount of time that the {@link DbPoolManager#getConnection} method
     *                    should block before throwing an exception when the pool is exhausted. Negative values
     *                    mean that the block can be infinite.
     */
    public Builder<DB> setMaxWaitTime(Duration maxWaitTime) {
      this.maxWaitMillis = maxWaitTime.toMillis();
      return this;
    }

    /**
     * Allow infinite block for the {@link DbPoolManager#getConnection} method.
     */
    public Builder<DB> enableInfiniteWait() {
      this.maxWaitMillis = -1L;
      return this;
    }

    /***
     * @param keepAliveTime The minimum amount of time the connection may sit idle in the pool before it is
     *                      eligible for eviction (with the extra condition that at least {@code minIdleConnections}
     *                      connections remain in the pool).
     */
    public Builder<DB> setKeepAliveTime(Duration keepAliveTime) {
      this.keepAliveMillis = keepAliveTime.toMillis();
      return this;
    }

    public Builder<DB> setMetricsTracking(boolean metricsTrackingEnabled) {
      this.metricsTrackingEnabled = metricsTrackingEnabled;
      return this;
    }

    @Override
    public TransactorImpl<DB> get() {
      return Builder.build(this);
    }

    private static <DB extends IDb> TransactorImpl<DB> build(Builder<DB> builder) {
      DbPoolManager<DB> dbPoolManager = new DbPoolManager<>(builder.dbConstructor, builder.maxTotalConnections,
          builder.minIdleConnections, builder.maxWaitMillis, builder.keepAliveMillis, builder.metricsTrackingEnabled);
      return new TransactorImpl<>(dbPoolManager, builder.metricsTrackingEnabled);
    }
  }
}
