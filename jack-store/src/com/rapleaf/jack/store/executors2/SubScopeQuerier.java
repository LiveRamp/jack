package com.rapleaf.jack.store.executors2;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;

public class SubScopeQuerier extends BaseExecutor2<JsRecords> {

  public SubScopeQuerier(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  @Override
  public JsRecords execute(IDb db) throws IOException {
    return null;
  }

}
