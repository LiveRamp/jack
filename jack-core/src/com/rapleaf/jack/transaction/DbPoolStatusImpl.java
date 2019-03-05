package com.rapleaf.jack.transaction;

import com.rapleaf.jack.IDb;
import org.apache.commons.pool2.ObjectPool;

public class DbPoolStatusImpl<DB extends IDb> implements DbPoolStatus {

  private final ObjectPool<DB> connectionPool;
  public DbPoolStatusImpl(ObjectPool<DB> connectionPool) {
    this.connectionPool = connectionPool;
  }

  @Override
  public int getNumActive() {
    return this.connectionPool.getNumActive();
  }

  @Override
  public int getNumIdle() {
    return this.connectionPool.getNumIdle();
  }

  @Override
  public int getNumWaiters() {
    return this.connectionPool.getNumActive();
  }
}
