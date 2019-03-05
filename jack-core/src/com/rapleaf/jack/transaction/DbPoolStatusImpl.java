package com.rapleaf.jack.transaction;

import com.rapleaf.jack.IDb;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class DbPoolStatusImpl<DB extends IDb> implements DbPoolStatus {

  private final GenericObjectPool<DB> connectionPool;
  public DbPoolStatusImpl(GenericObjectPool<DB> connectionPool) {
    this.connectionPool = connectionPool;
  }

  /**
   * @return The number of db connection object currently borrowed from this pool
   */
  @Override
  public int getNumActive() {
    return this.connectionPool.getNumActive();
  }

  /**
   * @return The number of db connection object currently idle in this pool
   */
  @Override
  public int getNumIdle() {
    return this.connectionPool.getNumIdle();
  }

  /**
   * @return The number of threads currently blocked waiting for an db connection object from the pool
   */
  @Override
  public int getNumWaiters() {
    return this.connectionPool.getNumWaiters();
  }
}
