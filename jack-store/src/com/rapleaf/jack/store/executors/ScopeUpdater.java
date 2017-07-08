package com.rapleaf.jack.store.executors;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;

public class ScopeUpdater extends BaseCreatorExecutor<JsRecord, ScopeUpdater> {

  ScopeUpdater(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  @Override
  JsRecord internalExecute(IDb db) throws IOException {
    if (!types.isEmpty()) {
      deleteExistingEntries(db, executionScopeId);
      insertNewEntries(db, executionScopeId);
    }
    return new JsRecord(executionScopeId, types, values);
  }

  @Override
  ScopeUpdater getSelf() {
    return this;
  }

}
