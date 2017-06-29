package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.transaction.ITransactor;

public class RecordDeletionExecutor<DB extends IDb> extends BaseExecutor<DB> {

  private final Set<String> keysToDelete;

  RecordDeletionExecutor(ITransactor<DB> transactor, JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(transactor, table, predefinedScope, predefinedScopeNames);
    this.keysToDelete = Sets.newHashSet();
  }

  public RecordDeletionExecutor<DB> delete(String key) {
    this.keysToDelete.add(key);
    return this;
  }

  public void execute() {
    if (keysToDelete.isEmpty()) {
      return;
    }

    Optional<JsScope> executionScope = getOptionalExecutionScope();
    if (!executionScope.isPresent()) {
      return;
    }

    transactor.executeAsTransaction(db -> {
      db.createDeletion()
          .from(table.table)
          .where(table.scopeColumn.equalTo(executionScope.get().getScopeId()))
          .where(table.keyColumn.in(keysToDelete))
          .execute();
    });
  }

}
