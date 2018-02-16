package com.rapleaf.jack.transaction;

import java.io.Closeable;

import com.rapleaf.jack.IDb;

public interface ITransactor<DB extends IDb> extends Closeable {

  /**
   * Run the next operation as a transaction.
   *
   * @return
   */
  ITransactor<DB> asTransaction();

  /**
   * If the operation fails, retries it up to numRetries before failing and returning the operation to the client.
   *
   * @param numRetries
   * @return
   */
  ITransactor<DB> withMaxRetry(int numRetries);

  <T> T query(IQuery<DB, T> query);

  @Deprecated
  <T> T queryAsTransaction(IQuery<DB, T> query);

  void execute(IExecution<DB> execution);

  @Deprecated
  void executeAsTransaction(IExecution<DB> execution);

  @Override
  void close();

  interface Builder<DB extends IDb, Impl extends ITransactor<DB>> {
    Impl get();
  }

}
