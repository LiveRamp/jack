package com.rapleaf.jack.store.executors2;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;

public class SubScopeCreator extends BaseExecutor2<JsRecord> {

  public SubScopeCreator(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  @Override
  public JsRecord execute(IDb db) throws IOException {
    return null;
  }

}
