package com.rapleaf.jack.transaction;

import java.io.Closeable;
import java.io.IOException;

import com.rapleaf.jack.IDb;

interface IDbManager<DB extends IDb> extends Closeable {

  DB getConnection(long timestamp);

  void returnConnection(DB connection);

  @Override
  void close() throws IOException;

}
