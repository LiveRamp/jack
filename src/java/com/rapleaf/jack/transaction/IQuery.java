package com.rapleaf.jack.transaction;

import com.rapleaf.jack.IDb;

@FunctionalInterface
public interface IQuery<DB extends IDb, T> {

  T query(DB db) throws Exception;

}
