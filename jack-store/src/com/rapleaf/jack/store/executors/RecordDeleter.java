package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbConstants;

public class RecordDeleter extends BaseDeleterExecutor<Void, Void, RecordDeleter> {

  RecordDeleter(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
  }

  @Override
  Void internalExecute(IDb db) throws IOException {
    return internalExec(db);
  }

  @Override
  Void internalExec(IDb db) throws IOException {
    if (deleteEntireRecord) {
      executeRecordDeletion(db);
    } else {
      executeKeyDeletion(db);
    }
    return null;
  }

  private void executeRecordDeletion(IDb db) throws IOException {
    Set<Long> nestedRecordIds = InternalScopeGetter.getNestedRecordIds(db, table, Collections.singleton(executionRecordId));
    if (!nestedRecordIds.isEmpty() && !allowRecursion) {
      throw new JackRuntimeException("There are nested scopes under the scopes to delete");
    }
    Set<Long> recordIdsToDelete = Sets.newHashSet(executionRecordId);
    recordIdsToDelete.addAll(nestedRecordIds);
    deleteScopes(db, recordIdsToDelete);
  }

  private void executeKeyDeletion(IDb db) throws IOException {
    if (deleteAllKeys) {
      db.createDeletion()
          .from(table.table)
          .where(table.scopeColumn.equalTo(executionRecordId))
          .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
          .execute();
    } else if (!keysToDelete.isEmpty()) {
      for (String key : keysToDelete) {
        db.createDeletion()
            .from(table.table)
            .where(table.scopeColumn.equalTo(executionRecordId))
            .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
            .where(table.keyColumn.equalTo(key).or(table.keyColumn.startsWith(key + JsonDbConstants.PATH_SEPARATOR)))
            .execute();
      }
    }
  }

  @Override
  RecordDeleter getSelf() {
    return this;
  }

}
