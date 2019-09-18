package com.rapleaf.jack.transaction;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.Records;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.ConnectionCreationFailureException;
import com.rapleaf.jack.exception.NoAvailableConnectionException;

class DbPoolManager<DB extends IDb> implements IDbManager<DB> {
  private static final Logger LOG = LoggerFactory.getLogger(DbPoolManager.class);

  public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 3;
  public static final int DEFAULT_MIN_IDLE_CONNECTIONS = 1;
  public static final long DEFAULT_MAX_WAIT_TIME = Duration.standardSeconds(30).getMillis();
  public static final long DEFAULT_KEEP_ALIVE_TIME = Duration.standardMinutes(5).getMillis();
  public static final boolean DEFAULT_METRICS_TRACKING_ENABLED = false;

  private final DbMetricsImpl metrics;
  private final boolean metricsTrackingEnabled;
  private final GenericObjectPool<DB> connectionPool;
  private final DbPoolStatus poolStatus;

  /**
   * Create a new DbPoolManager.
   *
   * @param dbConstructor       A callable that is used to create new db connections in the connection pool.
   * @param maxTotalConnections The maximum number of connections that can be created in the pool. Negative values
   *                            mean that there is no limit.
   * @param minIdleConnections  The minimum number of idle connections to keep in the pool.
   * @param maxWaitTime         The maximum amount of time that the {@link DbPoolManager#getConnection} method
   *                            should block before throwing an exception when the pool is exhausted. Negative values
   *                            mean that the block can be infinite.
   * @param keepAliveTime       The minimum amount of time the connection may sit idle in the pool before it is
   *                            eligible for eviction (with the extra condition that at least {@code minIdleConnections}
   *                            connections remain in the pool). When this parameter is less than zero, there is no
   *                            eviction.
   */
  DbPoolManager(Callable<DB> dbConstructor, int maxTotalConnections, int minIdleConnections, long maxWaitTime, long keepAliveTime, boolean metricsTrackingEnabled) {
    GenericObjectPoolConfig config = new GenericObjectPoolConfig();
    config.setFairness(true);
    config.setBlockWhenExhausted(true);
    config.setJmxEnabled(false);
    config.setLifo(false);
    config.setTestOnCreate(false);
    config.setTestOnReturn(false);
    config.setTestOnBorrow(true);
    config.setTestWhileIdle(true);

    config.setMaxTotal(maxTotalConnections);
    config.setMaxIdle(maxTotalConnections);
    config.setMinIdle(minIdleConnections);
    config.setMaxWaitMillis(maxWaitTime);

    // run eviction thread every keepAliveTime + 500 millis
    // add 500 millis to ensure the first round of eviction starts after
    // some connections have reached keep alive time
    config.setTimeBetweenEvictionRunsMillis(keepAliveTime + 500);

    // an idle connection can be evicted after keepAliveTime millis
    config.setSoftMinEvictableIdleTimeMillis(keepAliveTime);

    // after keepAliveTime, only minIdleConnections will be kept in the pool
    // this config may need further adjustment
    config.setNumTestsPerEvictionRun(maxTotalConnections - minIdleConnections);

    this.connectionPool = new GenericObjectPool<>(new DbPoolFactory<>(dbConstructor), config);
    this.metricsTrackingEnabled = metricsTrackingEnabled;
    this.metrics = new DbMetricsImpl(config.getMaxTotal(), config.getMinIdle(), config.getMaxWaitMillis(), config.getSoftMinEvictableIdleTimeMillis());
    this.poolStatus = new DbPoolStatusImpl(connectionPool);
  }

  /**
   * @throws NoAvailableConnectionException     if there is no available connections. Users can either
   *                                            increase the max total connections or max wait time.
   * @throws IllegalStateException              if the DB manager has already been closed.
   * @throws ConnectionCreationFailureException if new DB connections cannot be created.
   */
  @Override
  public DB getConnection() {
    try {
      if (metricsTrackingEnabled) {
        metrics.update(true, connectionPool);
      }
      DB connection = connectionPool.borrowObject();
      if (metricsTrackingEnabled) {
        metrics.update(false, connectionPool);
      }
      return connection;
    } catch (NoSuchElementException e) {
      String message = "No available connection; please consider increasing wait time or total connections";
      LOG.error(message, e);
      throw new NoAvailableConnectionException(message);
    } catch (IllegalStateException e) {
      String message = "DB connection pool has been closed";
      LOG.error(message, e);
      throw new IllegalStateException(message);
    } catch (Exception e) {
      throw new ConnectionCreationFailureException("DB connection creation failed", e);
    }
  }

  @Override
  public void returnConnection(DB connection) {
    try {
      if (metricsTrackingEnabled) {
        metrics.update(false, connectionPool);
      }
      connectionPool.returnObject(connection);
      metrics.update(false, connectionPool);
    } catch (Exception e) {
      LOG.error("Return connection failed", e);
    }
  }

  @Override
  public void invalidateConnection(final DB connection) {
    try {
      if (metricsTrackingEnabled) {
        metrics.update(false, connectionPool);
      }
      connectionPool.invalidateObject(connection);
      metrics.update(false, connectionPool);
    } catch (Exception e) {
      LOG.error("Failed to invalidate connection. This will likely lead to a connection leak!", e);
    }
  }

  @Override
  public void close() {
    connectionPool.close();
  }

  @Override
  public DbMetrics getMetrics() {
    if (!metricsTrackingEnabled) {
      return new MockDbMetrics();
    } else {
      metrics.update(false, connectionPool);
      return metrics;
    }
  }

  @VisibleForTesting
  ObjectPool<DB> getConnectionPool() {
    return connectionPool;
  }

  @Override
  public DbPoolStatus getDbPoolStatus() {
    return this.poolStatus;
  }

  private static class DbPoolFactory<DB extends IDb> implements PooledObjectFactory<DB> {
    private static final String VALIDATION_QUERY = "SELECT 1 AS id";
    private static final Collection<Column> VALIDATION_COLUMN_LIST =
        Collections.singletonList(Column.fromId(null));
    private final Callable<DB> dbConstructor;

    DbPoolFactory(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
    }

    @Override
    public PooledObject<DB> makeObject() throws Exception {
      DB connection = dbConstructor.call();
      connection.disableCaching();
      return new DefaultPooledObject<>(connection);
    }

    @Override
    public void destroyObject(PooledObject<DB> connection) throws IOException {
      connection.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<DB> connection) {
      try {
        Records records = connection.getObject()
            .findBySql(VALIDATION_QUERY, Collections.emptyList(), VALIDATION_COLUMN_LIST);
        return records.size() == 1;
      } catch (IOException e) {
        return false;
      }
    }

    @Override
    public void activateObject(PooledObject<DB> connection) throws Exception {
      // ignore
    }

    @Override
    public void passivateObject(PooledObject<DB> connection) throws Exception {
      connection.getObject().setAutoCommit(true);
      connection.getObject().setBulkOperation(false);
    }
  }

}
