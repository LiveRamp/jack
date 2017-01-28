package com.rapleaf.jack.transaction;

import com.rapleaf.jack.IDb;

public interface ITransaction<DB extends IDb> extends AutoCloseable {

  void commit();

  void rollback();

  @Override
  void close();

}
