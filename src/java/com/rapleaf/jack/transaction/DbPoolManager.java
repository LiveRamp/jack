package com.rapleaf.jack.transaction;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.ConnectionCreationFailureException;
import com.rapleaf.jack.exception.NoAvailableConnectionException;

class DbPoolManager<DB extends IDb> implements IDbManager<DB> {
  private static final Logger LOG = LoggerFactory.getLogger(DbPoolManager.class);

  private final ObjectPool<DB> connectionPool;

  DbPoolManager(Callable<DB> dbConstructor, int maxTotalConnections, int minIdleConnections, long maxWaitTime, long keepAliveTime) {
    GenericObjectPoolConfig config = new GenericObjectPoolConfig();
    config.setFairness(true);
    config.setBlockWhenExhausted(true);
    config.setJmxEnabled(false);
    config.setLifo(false);
    config.setTestOnCreate(false);
    config.setTestOnBorrow(false);
    config.setTestOnReturn(false);
    config.setMaxTotal(maxTotalConnections);
    config.setMinIdle(minIdleConnections);
    config.setMaxWaitMillis(maxWaitTime);
    config.setTimeBetweenEvictionRunsMillis(keepAliveTime);
    config.setSoftMinEvictableIdleTimeMillis(keepAliveTime);
    this.connectionPool = new GenericObjectPool<DB>(new DbPoolFactory<DB>(dbConstructor), config);
  }

  @Override
  public DB getConnection(long timestamp) {
    try {
      return connectionPool.borrowObject();
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
      connectionPool.returnObject(connection);
    } catch (Exception e) {
      LOG.error("Return connection failed", e);
    }
  }

  @Override
  public void close() {
    connectionPool.close();
  }

  private static class DbPoolFactory<DB extends IDb> implements PooledObjectFactory<DB> {
    private final Callable<DB> dbConstructor;

    DbPoolFactory(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
    }

    @Override
    public PooledObject<DB> makeObject() throws Exception {
      DB connection = dbConstructor.call();
      connection.disableCaching();
      return new DefaultPooledObject<DB>(connection);
    }

    @Override
    public void destroyObject(PooledObject<DB> connection) throws Exception {
      connection.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<DB> connection) {
      return true;
    }

    @Override
    public void activateObject(PooledObject<DB> connection) throws Exception {
      // ignore
    }

    @Override
    public void passivateObject(PooledObject<DB> connection) throws Exception {
      // ignore
    }
  }

}
