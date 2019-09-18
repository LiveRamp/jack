package com.rapleaf.jack.transaction;

import java.io.Closeable;

import com.rapleaf.jack.IDb;

interface IDbManager<DB extends IDb> extends Closeable {

  DB getConnection();

  /**
   * Returns a connection to the connection pool, potentially allowing it to be reused in the
   * future.
   */
  void returnConnection(DB connection);

  /**
   * Explicitly signals that the pooled connection is being returned, but should not be used in any
   * way in the future.
   */
  void invalidateConnection(DB connection);

  @Override
  void close();

  DbMetrics getMetrics();

  DbPoolStatus getDbPoolStatus();

}
