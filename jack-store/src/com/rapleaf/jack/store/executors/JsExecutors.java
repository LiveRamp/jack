package com.rapleaf.jack.store.executors;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.transaction.ITransactor;

public class JsExecutors<DB extends IDb> {

  private final ITransactor<DB> transactor;
  private final JsTable jsTable;
  private final Optional<JsScope> predefinedScope;
  private final List<String> predefinedScopeNames;

  public JsExecutors(ITransactor<DB> transactor, JsTable jsTable, JsScope executionScope) {
    this.transactor = transactor;
    this.jsTable = jsTable;
    this.predefinedScope = Optional.of(executionScope);
    this.predefinedScopeNames = Collections.emptyList();
  }

  public JsExecutors(ITransactor<DB> transactor, JsTable jsTable, List<String> executionScopeNames) {
    this.transactor = transactor;
    this.jsTable = jsTable;
    this.predefinedScope = Optional.empty();
    this.predefinedScopeNames = executionScopeNames;
  }

  ScopeCreationExecutor<DB> createScope(String scope) {
    return new ScopeCreationExecutor<>(transactor, jsTable, predefinedScope, predefinedScopeNames, scope);
  }

  ScopeRenameExecutor<DB> renameScope(String currentName, String newName) {
    return new ScopeRenameExecutor<>(transactor, jsTable, predefinedScope, predefinedScopeNames, currentName, newName);
  }

  ScopeQueryExecutor<DB> queryScope() {
    return new ScopeQueryExecutor<>(transactor, jsTable, predefinedScope, predefinedScopeNames);
  }

  ScopeDeletionExecutor<DB> deleteScope() {

    return new ScopeDeletionExecutor<>(transactor, jsTable, predefinedScope, predefinedScopeNames);
  }

}
