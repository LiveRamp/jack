package com.rapleaf.jack.store.executors2;

import com.rapleaf.jack.store.JsTable;

/**
 * jackStore.scope().create() -> create scope and add records
 * jackStore.scope().get()    -> get all records under scope
 * jackStore.scope().update() -> rename scope, update records under scope
 * jackStore.scope().delete() -> delete scope and records under scope
 * <p>
 * jackStore.scope().createSubScope() -> new subscope under scope and records under new subscope
 * jackStore.scope().querySubScopes() -> records in subscopes under scope
 * jackStore.scope().updateSubscopes() -> update records under subscopes under scope
 * jackStore.scope().deleteSubscopes() -> delete records under subscopes under scope
 */
public class JsExecutors2 {

  private final JsTable table;
  private final Long executionScopeId;

  public JsExecutors2(JsTable table, Long executionScopeId) {
    this.table = table;
    this.executionScopeId = executionScopeId;
  }

  public ScopeReader read() {
    return new ScopeReader(table, executionScopeId);
  }

  public ScopeUpdater update() {
    return new ScopeUpdater(table, executionScopeId);
  }

  public ScopeDeleter delete() {
    return new ScopeDeleter(table, executionScopeId);
  }

  public SubScopeCreator createSubScope() {
    return new SubScopeCreator(table, executionScopeId);
  }

  public SubScopeQuerier querySubScopes() {
    return new SubScopeQuerier(table, executionScopeId);
  }

  public SubScopeUpdater updateSubScopes() {
    return new SubScopeUpdater(table, executionScopeId);
  }

  public SubScopeDeleter deleteSubScopes() {
    return new SubScopeDeleter(table, executionScopeId);
  }

}
