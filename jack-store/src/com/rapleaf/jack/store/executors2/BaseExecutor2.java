package com.rapleaf.jack.store.executors2;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsTable;

public abstract class BaseExecutor2<T> {

  protected final JsTable table;
  protected final Long executionScopeId;

  public BaseExecutor2(JsTable table, Long executionScopeId) {
    this.table = table;
    this.executionScopeId = executionScopeId;
  }

  abstract public T execute(IDb db) throws IOException;

}
