package com.rapleaf.jack.store.executors;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

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

  public SubScopeCreationExecutor<DB> createSubScope(String subScopeName) {
    return new SubScopeCreationExecutor<>(jsTable, predefinedScope, predefinedScopeNames, subScopeName);
  }

  public SubScopeCreationExecutor<DB> createSubScope() {
    return new SubScopeCreationExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

  public SubScopeModificationExecutor<DB> renameSubScope(String currentName, String newName) {
    return new SubScopeModificationExecutor<>(jsTable, predefinedScope, predefinedScopeNames, currentName, newName);
  }

  public SubScopeGetterExecutor<DB> getSubScope(long subScopeId) {
    return new SubScopeGetterExecutor<>(jsTable, predefinedScope, predefinedScopeNames, subScopeId);
  }

  public SubScopeGetterExecutor<DB> getSubScope(String subScopeName) {
    return new SubScopeGetterExecutor<>(jsTable, predefinedScope, predefinedScopeNames, subScopeName);
  }

  public SubScopeQueryExecutor<DB> querySubScope() {
    return new SubScopeQueryExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

  public SubScopeDeletionExecutor<DB> deleteSubScope() {
    return new SubScopeDeletionExecutor<>(jsTable, predefinedScope, predefinedScopeNames);
  }

  public SubScopeReaderExecutor<DB> readSubScope(Collection<JsScope> subScopes) {
    return new SubScopeReaderExecutor<>(jsTable, predefinedScope, predefinedScopeNames, subScopes.stream().map(JsScope::getScopeId).collect(Collectors.toSet()));
  }

  public SubScopeReaderExecutor<DB> readSubScope(JsScopes subScopes) {
    return new SubScopeReaderExecutor<>(jsTable, predefinedScope, predefinedScopeNames, Sets.newHashSet(subScopes.getScopeIds()));
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
