package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsTable;

/**
 * Delete records under the execution scope
 */
public class RecordDeletionExecutor<DB extends IDb> extends BaseExecutor<DB> {

  private final Set<String> keysToDelete;

  RecordDeletionExecutor(JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(table, predefinedScope, predefinedScopeNames);
    this.keysToDelete = Sets.newHashSet();
  }

  public RecordDeletionExecutor<DB> delete(String key) {
    this.keysToDelete.add(key);
    return this;
  }

  public void execute(DB db) throws IOException {
    if (keysToDelete.isEmpty()) {
      return;
    }
    Optional<JsScope> executionScope = getOptionalExecutionScope(db);
    if (!executionScope.isPresent()) {
      return;
    }

    db.createDeletion()
        .from(table.table)
        .where(table.scopeColumn.equalTo(executionScope.get().getScopeId()))
        .where(table.keyColumn.in(keysToDelete))
        .execute();
  }

}
