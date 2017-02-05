package com.rapleaf.jack.transaction;

import java.util.concurrent.Callable;

import org.joda.time.Duration;

import com.rapleaf.jack.IDb;

class FixedDbManager<DB extends IDb> extends AbstractDbManager<DB> {

  private final int maxConnections;

  FixedDbManager(Callable<DB> callable, int maxConnections, Duration timeout) {
    super(callable, timeout);
    this.maxConnections = maxConnections;
  }

  @Override
  protected boolean isConnectionMaximized() {
    return allConnections.size() >= maxConnections;
  }

}