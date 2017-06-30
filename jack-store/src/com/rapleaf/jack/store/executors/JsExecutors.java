package com.rapleaf.jack.store.executors;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsScopes;
import com.rapleaf.jack.store.JsTable;

public class JsExecutors<DB extends IDb> {

  private final JsTable jsTable;
  private final Optional<JsScope> predefinedScope;
  private final List<String> predefinedScopeNames;

  public JsExecutors(JsTable jsTable, JsScope executionScope) {
    this.jsTable = jsTable;
    this.predefinedScope = Optional.of(executionScope);
    this.predefinedScopeNames = Collections.emptyList();
  }

  public JsExecutors(JsTable jsTable, List<String> executionScopeNames) {
    this.jsTable = jsTable;
    this.predefinedScope = Optional.empty();
    this.predefinedScopeNames = executionScopeNames;
  }

  public ScopeCreationExecutor<DB> createSubScope(String subScopeName) {
    return new ScopeCreationExecutor<>(jsTable, predefinedScope, predefinedScopeNames, subScopeName);
  }

  public ScopeCreationExecutor<DB> createSubScope() {
    return new ScopeCreationExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

  public ScopeModificationExecutor<DB> renameSubScope(String currentName, String newName) {
    return new ScopeModificationExecutor<>(jsTable, predefinedScope, predefinedScopeNames, currentName, newName);
  }

  public ScopeGetterExecutor<DB> getSubScope(long subScopeId) {
    return new ScopeGetterExecutor<>(jsTable, predefinedScope, predefinedScopeNames, subScopeId);
  }

  public ScopeGetterExecutor<DB> getSubScope(String subScopeName) {
    return new ScopeGetterExecutor<>(jsTable, predefinedScope, predefinedScopeNames, subScopeName);
  }

  public ScopeQueryExecutor<DB> querySubScope() {
    return new ScopeQueryExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

  public ScopeDeletionExecutor<DB> deleteSubScope() {
    return new ScopeDeletionExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

  public ScopeReaderExecutor<DB> readSubScope(Collection<JsScope> subScopes) {
    return new ScopeReaderExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

  public ScopeReaderExecutor<DB> readSubScope(JsScopes subScope) {
    return new ScopeReaderExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

  public RecordReaderExecutor<DB> readScope() {
    return new RecordReaderExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

  public RecordIndexExecutor<DB> indexRecord() {
    return new RecordIndexExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

  public RecordDeletionExecutor<DB> deleteRecord() {
    return new RecordDeletionExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

}
