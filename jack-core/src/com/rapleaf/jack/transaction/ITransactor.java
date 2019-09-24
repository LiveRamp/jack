package com.rapleaf.jack.transaction;

import java.io.Closeable;

import com.rapleaf.jack.IDb;

public interface ITransactor<DB extends IDb> extends Closeable {

  ITransactor<DB> asTransaction();

  ITransactor<DB> allowRetries(RetryPolicy retryPolicy);

  <T> T query(IQuery<DB, T> query);

  void execute(IExecution<DB> execution);

  @Deprecated
  <T> T queryAsTransaction(IQuery<DB, T> query);

  @Deprecated
  void executeAsTransaction(IExecution<DB> execution);

  @Override
  void close();

  DbPoolStatus getDbPoolStatus();

  interface Builder<DB extends IDb, Impl extends ITransactor<DB>> {
    Impl get();
  }

  interface RetryPolicy {

    boolean shouldRetry();

    void updateOnFailure();

    void updateOnSuccess();

    void execute();
  }
}
