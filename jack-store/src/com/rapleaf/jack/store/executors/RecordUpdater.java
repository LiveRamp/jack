package com.rapleaf.jack.store.executors;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;

public class RecordUpdater extends BaseCreatorExecutor<JsRecord, RecordUpdater> {

  RecordUpdater(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
  }

  @Override
  JsRecord internalExecute(IDb db) throws IOException {
    if (!types.isEmpty()) {
      deleteExistingEntries(db, executionRecordId);
      insertNewEntries(db, executionRecordId);
    }
    return new JsRecord(executionRecordId, types, values);
  }

  @Override
  RecordUpdater getSelf() {
    return this;
  }

}
