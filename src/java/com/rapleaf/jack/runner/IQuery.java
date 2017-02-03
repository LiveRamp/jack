package com.rapleaf.jack.runner;

import java.io.IOException;

import com.rapleaf.jack.IDb;

@FunctionalInterface
public interface IQuery<DB extends IDb, T> {

  T query(DB db) throws IOException;

}
