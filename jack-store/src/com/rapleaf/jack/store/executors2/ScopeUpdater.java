package com.rapleaf.jack.store.executors2;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsTable;

public class ScopeUpdater extends BaseCreatorExecutor2<ScopeUpdater> {

  ScopeUpdater(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  @Override
  ScopeUpdater getSelf() {
    return this;
  }

  @Override
  Long getScopeId(IDb db) {
    return executionScopeId;
  }

}
