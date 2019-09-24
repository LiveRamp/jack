package com.rapleaf.jack.transaction;

import java.time.Duration;
import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.util.ExponentialBackoffRetryPolicy;

/**
 * If there is any exception while executing the query, throws
 * {@link com.rapleaf.jack.exception.SqlExecutionFailureException}.
 * <p>
 * If there is no available connections, throws {@link com.rapleaf.jack.exception.NoAvailableConnectionException}.
 * Users can either increase the max total connections or max wait time.
 * <p>
 * If the DB manager has already been closed, throws {@link IllegalStateException}.
 * <p>
 * If new DB connections cannot be created, throws
 * {@link com.rapleaf.jack.exception.ConnectionCreationFailureException}.
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

  ITransactor<DB> newContext() {
    return new ExecutionContext();
  }

  @Override
  public ITransactor<DB> asTransaction() {
    return newContext().asTransaction();
  }

  @Override
  public ITransactor<DB> allowRetries(RetryPolicy retryPolicy) {
    return newContext().allowRetries(retryPolicy);
  }

  @Override
  public <T> T query(IQuery<DB, T> query) {
    return newContext().query(query);
  }

  @Deprecated
  @Override
  public <T> T queryAsTransaction(IQuery<DB, T> query) {
    return asTransaction().query(query);
  }

  @Override
  public void execute(IExecution<DB> execution) {
    newContext().execute(execution);
  }

  @Deprecated
  @Override
  public void executeAsTransaction(IExecution<DB> execution) {
    asTransaction().execute(execution);
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

  private <T> T query(IQuery<DB, T> query, ExecutionContext context) {
    while (true) {
      DB connection = dbManager.getConnection();
      boolean connectionSafeToReturn = true;
      try {
        connection.setAutoCommit(!context.asTransaction);
        long startTime = System.currentTimeMillis();
        T value = query.query(connection);
        if (context.asTransaction) {
          connection.commit();
        }
        long executionTime = System.currentTimeMillis() - startTime;
        context.retryPolicy.updateOnSuccess();
        if (metricsTrackingEnabled) {
          queryMetrics.update(executionTime, Thread.currentThread().getStackTrace()[3]);
        }
        return value;
      } catch (Exception e) {
        LOG.error("SQL execution failure", e);
        if (context.asTransaction) {
          connectionSafeToReturn = tryToSafelyRollback(connection);
        }

        context.retryPolicy.updateOnFailure();
        if (!context.retryPolicy.shouldRetry()) {
          throw new SqlExecutionFailureException(e);
        }
      } catch (Throwable t) {
        // We still try to explicitly rollback the transaction if a throwable is thrown.
        if (context.asTransaction) {
          connectionSafeToReturn = tryToSafelyRollback(connection);
        }

        throw t;
      } finally {
        if (connectionSafeToReturn) {
          dbManager.returnConnection(connection);
        } else {
          dbManager.invalidateConnection(connection);
        }
        context.retryPolicy.execute();
      }
    }
  }

  /**
   * @param connection The connection to rollback
   * @return True if the connection was successfully rolled back. False if it failed to rollback.
   */
  private boolean tryToSafelyRollback(DB connection) {
    try {
      connection.rollback();
    } catch (Exception e) {
      LOG.warn("Failed to rollback an active transaction", e);
      return false;
    }

    return true;
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

  class ExecutionContext implements ITransactor<DB> {

    boolean asTransaction = false;
    RetryPolicy retryPolicy = new NoRetryPolicy();

    @Override
    public ITransactor<DB> asTransaction() {
      asTransaction = true;
      return this;
    }

    @Override
    public ITransactor<DB> allowRetries(RetryPolicy retryPolicy) {
      this.retryPolicy = retryPolicy;
      return this;
    }

    @Override
    public <T> T query(IQuery<DB, T> query) {
      return TransactorImpl.this.query(query, this);
    }

    @Override
    public void execute(IExecution<DB> execution) {
      query(db -> {
        execution.execute(db);
        return null;
      });
    }

    @Deprecated
    @Override
    public <T> T queryAsTransaction(IQuery<DB, T> query) {
      return asTransaction().query(query);
    }

    @Deprecated
    @Override
    public void executeAsTransaction(IExecution<DB> execution) {
      asTransaction().execute(execution);
    }

    @Override
    public void close() {
      TransactorImpl.this.close();
    }

    @Override
    public DbPoolStatus getDbPoolStatus() {
      return TransactorImpl.this.getDbPoolStatus();
    }
  }

  class NoRetryPolicy extends ExponentialBackoffRetryPolicy {}

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
