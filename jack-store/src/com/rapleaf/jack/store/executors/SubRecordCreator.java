package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.UUID;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

public class SubRecordCreator extends BaseCreatorExecutor<JsRecord, Long, SubRecordCreator> {

  private String scopeName = null;

  SubRecordCreator(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
  }

  public SubRecordCreator recordName(String name) {
    Preconditions.checkArgument(name != null && !name.isEmpty(), "Scope name cannot be null or empty");
    this.scopeName = name;
    return this;
  }

  @Override
  JsRecord internalExecute(IDb db) throws IOException {
    Long recordId = internalExec(db);
    return new JsRecord(recordId, types, values);
  }

  @Override
  Long internalExec(IDb db) throws IOException {
    Long recordId = createNewScope(db);
    if (!types.isEmpty()) {
      insertNewEntries(db, recordId);
    }
    return recordId;
  }

  private Long createNewScope(IDb db) throws IOException {
    if (scopeName == null) {
      scopeName = UUID.randomUUID().toString();
    }
    return db.createInsertion()
        .into(table.table)
        .set(table.scope, executionRecordId)
        .set(table.key, JsConstants.SCOPE_KEY)
        .set(table.type, ValueType.SCOPE.value)
        .set(table.value, scopeName)
        .execute()
        .getFirstId();
  }

  @Override
  SubRecordCreator getSelf() {
    return this;
  }

}
