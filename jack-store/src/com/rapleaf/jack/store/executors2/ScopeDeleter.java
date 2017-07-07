package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.json.JsonDbConstants;

public class ScopeDeleter extends BaseDeleterExecutor2<Void, ScopeDeleter> {

  ScopeDeleter(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  @Override
  Void internalExecute(IDb db) throws IOException {
    if (deleteEntireRecord) {
      executeRecordDeletion(db);
    } else {
      executeKeyDeletion(db);
    }
    return null;
  }

  private void executeRecordDeletion(IDb db) throws IOException {
    Set<Long> nestedScopeIds = InternalScopeGetter.getNestedScopeIds(db, table, Collections.singleton(executionScopeId));
    if (!nestedScopeIds.isEmpty() && !allowRecursion) {
      throw new JackRuntimeException("There are nested scopes under the scopes to delete");
    }
    Set<Long> scopeIdsToDelete = Sets.newHashSet(executionScopeId);
    scopeIdsToDelete.addAll(nestedScopeIds);
    deleteScopes(db, scopeIdsToDelete);
  }

  private void executeKeyDeletion(IDb db) throws IOException {
    if (deleteAllKeys) {
      db.createDeletion()
          .from(table.table)
          .where(table.scopeColumn.equalTo(executionScopeId))
          .execute();
    } else if (!keysToDelete.isEmpty()) {
      for (String key : keysToDelete) {
        db.createDeletion()
            .from(table.table)
            .where(table.scopeColumn.equalTo(executionScopeId))
            .where(table.keyColumn.equalTo(key).or(table.keyColumn.startsWith(key + JsonDbConstants.PATH_SEPARATOR)))
            .execute();
      }
    }
  }

  @Override
  ScopeDeleter getSelf() {
    return this;
  }

}
