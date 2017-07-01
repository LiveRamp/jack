package com.rapleaf.jack.store.executors2;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsTable;

public class ScopeDeleter extends BaseExecutor2<Void> {
  public ScopeDeleter(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  @Override
  public Void execute(IDb db) throws IOException {
    return null;
  }

}
