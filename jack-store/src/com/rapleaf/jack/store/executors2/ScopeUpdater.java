package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

public class ScopeUpdater extends BaseCreatorExecutor2<ScopeUpdater> {

  ScopeUpdater(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  @Override
  ScopeUpdater getSelf() {
    return this;
  }

  @Override
  Long getScopeId() {
    return executionScopeId;
  }

  @Override
  public JsRecord execute(IDb db) throws IOException {
    Long scopeId = getScopeId();
    if (!types.isEmpty()) {
      deleteExistingEntries(db, scopeId);
      insertNewEntries(db, scopeId);
    }
    return new JsRecord(scopeId, types, values);
  }

}
