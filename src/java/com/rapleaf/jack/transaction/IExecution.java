package com.rapleaf.jack.transaction;

import com.rapleaf.jack.IDb;

@FunctionalInterface
public interface IExecution<DB extends IDb> {

  void execute(DB db) throws Exception;

}
