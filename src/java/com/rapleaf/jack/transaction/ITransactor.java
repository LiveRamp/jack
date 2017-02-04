package com.rapleaf.jack.transaction;

import java.io.Closeable;
import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.ConnectionCreationFailureException;

public interface ITransactor<DB extends IDb> extends Closeable {

  <T> T query(IQuery<DB, T> query) throws ConnectionCreationFailureException;

  void execute(IExecution<DB> execution) throws ConnectionCreationFailureException;

  @Override
  void close() throws IOException;

  interface Builder<DB extends IDb, Impl extends ITransactor<DB>> {
    Impl get();
  }

}
