package com.rapleaf.jack.transaction;

import java.io.Closeable;
import java.io.IOException;

import com.rapleaf.jack.IDb;

public interface ITransactor<DB extends IDb> extends Closeable {

  <T> T query(IQuery<DB, T> query);

  void execute(IExecution<DB> execution);

  @Override
  void close() throws IOException;

  interface Builder<DB extends IDb, Impl extends ITransactor<DB>> {
    Impl get();
  }

}
