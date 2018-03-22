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
   * If the next operation fails, retries it up to numRetries before failing and returning the operation to the client.
   * Note that this does not take into account the initial attempt i.e: setting this number to 1 would result in a
   * maximum of 2 total executions.
   *
   * @param numRetries
   * @return
   */
  ITransactor<DB> withMaxRetry(int numRetries);

  <T> T query(IQuery<DB, T> query);

  /**
   * @deprecated use {@link #asTransaction()}
   */
  @Deprecated
  <T> T queryAsTransaction(IQuery<DB, T> query);

  void execute(IExecution<DB> execution);

  /**
   * @deprecated Use {@link #asTransaction}
   */
  @Deprecated
  void executeAsTransaction(IExecution<DB> execution);

  @Override
  void close();

  interface Builder<DB extends IDb, Impl extends ITransactor<DB>> {
    Impl get();
  }

}
