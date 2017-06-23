package com.rapleaf.jack.transaction;

import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.SqlExecutionFailureException;

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

  private final int queryLogSize = 10;

  private TransactorMetricsImpl queryMetrics = new TransactorMetricsImpl(queryLogSize);

  private boolean metricsTrackingEnabled = DbPoolManager.DEFAULT_METRICS_TRACKING_ENABLED;

  TransactorImpl(IDbManager<DB> dbManager, boolean metricsTrackingEnabled) {
    this.dbManager = dbManager;
    this.metricsTrackingEnabled = metricsTrackingEnabled;
  }

  public static <DB extends IDb> Builder<DB> create(Callable<DB> dbConstructor) {
    return new Builder<>(dbConstructor);
  }

  @Deprecated
  @Override
  public <T> T execute(IQuery<DB, T> query) {
    return query(query);
  }

  @Override
  public <T> T query(IQuery<DB, T> query) {
    return query(query, false);
  }

  @Deprecated
  @Override
  public <T> T executeAsTransaction(IQuery<DB, T> query) {
    return queryAsTransaction(query);
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
      LOG.info("the transactor metrics aren't being tracked, it's useless to access them");
    }
    return queryMetrics;
  }

  DbMetrics getDbMetrics() {
    return dbManager.getMetrics();
  }

  private <T> T query(IQuery<DB, T> query, boolean asTransaction) {
    DB connection = dbManager.getConnection();
    connection.setAutoCommit(!asTransaction);
    try {
      long startTime = System.currentTimeMillis();
      T value = query.query(connection);
      if (asTransaction) {
        connection.commit();
      }
      long executionTime = System.currentTimeMillis() - startTime;
      queryMetrics.update(executionTime, Thread.currentThread().getStackTrace()[3]);
      return value;
    } catch (Exception e) {
      LOG.error("SQL execution failure", e);
      if (asTransaction) {
        connection.rollback();
      }
      throw new SqlExecutionFailureException(e);
    } finally {
      // connection should be reset in PooledObjectFactory
      dbManager.returnConnection(connection);
    }
  }

  private void execute(IExecution<DB> execution, boolean asTransaction) {
    DB connection = dbManager.getConnection();
    connection.setAutoCommit(!asTransaction);
    try {
      long startTime = System.currentTimeMillis();
      execution.execute(connection);
      if (asTransaction) {
        connection.commit();
      }
      long executionTime = System.currentTimeMillis() - startTime;
      queryMetrics.update(executionTime, Thread.currentThread().getStackTrace()[3]);
    } catch (Exception e) {
      LOG.error("SQL execution failure", e);
      if (asTransaction) {
        connection.rollback();
      }
      throw new SqlExecutionFailureException(e);
    } finally {
      // connection should be reset in PooledObjectFactory
      dbManager.returnConnection(connection);
    }
  }

  @Override
  public void close() {
    if (metricsTrackingEnabled) {
      LOG.info(dbManager.getMetrics().getSummary());
      LOG.info("" + "\n" + (queryMetrics.getSummary()));
    }
    dbManager.close();
  }

  public static class Builder<DB extends IDb> implements ITransactor.Builder<DB, TransactorImpl<DB>> {

    private final Callable<DB> dbConstructor;
    private int maxTotalConnections;
    private int minIdleConnections;
    private long maxWaitMillis;
    private long keepAliveMillis;
    private boolean metricsTrackingEnabled;

    Builder(Callable<DB> dbConstructor) {
      initialize();
      this.dbConstructor = dbConstructor;
    }

    /**
     * (Re)initialize the parameters to allow builder reuse.
     */
    private void initialize() {
      this.maxTotalConnections = DbPoolManager.DEFAULT_MAX_TOTAL_CONNECTIONS;
      this.minIdleConnections = DbPoolManager.DEFAULT_MIN_IDLE_CONNECTIONS;
      this.maxWaitMillis = DbPoolManager.DEFAULT_MAX_WAIT_TIME;
      this.keepAliveMillis = DbPoolManager.DEFAULT_KEEP_ALIVE_TIME;
      this.metricsTrackingEnabled = DbPoolManager.DEFAULT_METRICS_TRACKING_ENABLED;
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
      this.maxWaitMillis = maxWaitTime.getMillis();
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
      this.keepAliveMillis = keepAliveTime.getMillis();
      return this;
    }

    public Builder<DB> setMetricsTracking(boolean metricsTrackingEnabled) {
      this.metricsTrackingEnabled = metricsTrackingEnabled;
      return this;
    }

    /**
     * Returns a new transactor impl using the parameters specified during the building
     * process. After building, builder parameters are re-initialized and not shared
     * amongst built instances.
     */
    @Override
    public TransactorImpl<DB> get() {
      TransactorImpl<DB> transactor = Builder.build(this);
      initialize();
      return transactor;
    }

    private static <DB extends IDb> TransactorImpl<DB> build(Builder<DB> builder) {
      DbPoolManager<DB> dbPoolManager = new DbPoolManager<DB>(builder.dbConstructor, builder.maxTotalConnections,
          builder.minIdleConnections, builder.maxWaitMillis, builder.keepAliveMillis, builder.metricsTrackingEnabled);
      return new TransactorImpl<DB>(dbPoolManager, builder.metricsTrackingEnabled);
    }
  }
}
