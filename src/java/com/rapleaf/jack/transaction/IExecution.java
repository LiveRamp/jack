package com.rapleaf.jack.transaction;

import java.io.IOException;

import com.rapleaf.jack.IDb;

@FunctionalInterface
public interface IExecution<DB extends IDb> {

  void execute(DB db) throws IOException;

}
