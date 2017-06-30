package com.rapleaf.jack.store.executors;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsScopes;
import com.rapleaf.jack.store.JsTable;

public class JsExecutors {

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

  public SubScopeCreationExecutor createSubScope(String subScopeName) {
    return new SubScopeCreationExecutor(jsTable, predefinedScope, predefinedScopeNames, subScopeName);
  }

  public SubScopeCreationExecutor createSubScope() {
    return new SubScopeCreationExecutor(jsTable, predefinedScope, predefinedScopeNames);
  }

  public SubScopeModificationExecutor renameSubScope(String currentName, String newName) {
    return new SubScopeModificationExecutor(jsTable, predefinedScope, predefinedScopeNames, currentName, newName);
  }

  public SubScopeGetterExecutor getSubScope(long subScopeId) {
    return new SubScopeGetterExecutor(jsTable, predefinedScope, predefinedScopeNames, subScopeId);
  }

  public SubScopeGetterExecutor getSubScope(String subScopeName) {
    return new SubScopeGetterExecutor(jsTable, predefinedScope, predefinedScopeNames, subScopeName);
  }

  public SubScopeQueryExecutor querySubScopes() {
    return new SubScopeQueryExecutor(jsTable, predefinedScope, predefinedScopeNames);
  }

  public SubScopeDeletionExecutor deleteSubScopes() {
    return new SubScopeDeletionExecutor(jsTable, predefinedScope, predefinedScopeNames);
  }

  public SubScopeReaderExecutor readSubScopesWithIds(Set<Long> subScopeIds) {
    return new SubScopeReaderExecutor(jsTable, predefinedScope, predefinedScopeNames, subScopeIds);
  }

  public SubScopeReaderExecutor readSubScopes(Collection<JsScope> subScopes) {
    return new SubScopeReaderExecutor(jsTable, predefinedScope, predefinedScopeNames, subScopes.stream().map(JsScope::getScopeId).collect(Collectors.toSet()));
  }

  public SubScopeReaderExecutor readSubScopes(JsScopes subScopes) {
    return new SubScopeReaderExecutor(jsTable, predefinedScope, predefinedScopeNames, Sets.newHashSet(subScopes.getScopeIds()));
  }

  public RecordReaderExecutor readScope() {
    return new RecordReaderExecutor(jsTable, predefinedScope, predefinedScopeNames);
  }

  public RecordIndexExecutor indexRecords() {
    return new RecordIndexExecutor(jsTable, predefinedScope, predefinedScopeNames);
  }

  public RecordDeletionExecutor deleteRecords() {
    return new RecordDeletionExecutor(jsTable, predefinedScope, predefinedScopeNames);
  }

}
