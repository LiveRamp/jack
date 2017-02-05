package com.rapleaf.jack.transaction;

import java.util.concurrent.Callable;

import org.joda.time.Duration;

import com.rapleaf.jack.IDb;

class FlexibleDbManager<DB extends IDb> extends AbstractDbManager<DB> {

  FlexibleDbManager(Callable<DB> callable, Duration timeout) {
    super(callable, timeout);
  }

  @Override
  protected boolean isConnectionMaximized() {
    return false;
  }

}
