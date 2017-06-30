package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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
public class RecordDeletionExecutor extends BaseExecutor {

  private final Set<String> keysToDelete;

  RecordDeletionExecutor(JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(table, predefinedScope, predefinedScopeNames);
    this.keysToDelete = Sets.newHashSet();
  }

  public RecordDeletionExecutor delete(String keyToDelete, String... keysToDelete) {
    this.keysToDelete.add(keyToDelete);
    this.keysToDelete.addAll(Arrays.asList(keysToDelete));
    return this;
  }

  public RecordDeletionExecutor delete(Collection<String> keysToDelete) {
    this.keysToDelete.addAll(keysToDelete);
    return this;
  }

  public void execute(IDb db) throws IOException {
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
