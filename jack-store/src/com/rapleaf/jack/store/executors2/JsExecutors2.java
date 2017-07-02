package com.rapleaf.jack.store.executors2;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;

public class JsExecutors2 {

  private final JsTable table;
  private final Long executionScopeId;

  public JsExecutors2(JsTable table, Long executionScopeId) {
    this.table = table;
    this.executionScopeId = executionScopeId;
  }

  /**
   * Query key value pairs under the current scope.
   *
   * @return {@link JsRecord}
   */
  public ScopeInquirer query() {
    return new ScopeInquirer(table, executionScopeId);
  }

  /**
   * Insert or update key value pairs under the current scope.
   *
   * @return {@link JsRecord}
   */
  public ScopeUpdater update() {
    return new ScopeUpdater(table, executionScopeId);
  }

  /**
   * Delete key value pairs under the current scope.
   * Delete the current scope.
   *
   * @return {@link Void}
   */
  public ScopeDeleter delete() {
    return new ScopeDeleter(table, executionScopeId);
  }

  /**
   * Create a new scope under the current scope.
   * Insert key value pairs under the new scope.
   *
   * @return {@link JsRecord}
   */
  public SubScopeCreator createSubScope() {
    return new SubScopeCreator(table, executionScopeId);
  }

  /**
   * Query the sub scopes under the current scope.
   * The result set does not include key value pairs directly under the current scope.
   *
   * @return {@link JsRecords} representing the fetched sub scopes
   */
  public SubScopeInquirer querySubScopes() {
    return new SubScopeInquirer(table, SubScopeInquirer.DEFAULT_SKIP_SUB_SCOPE_VALIDATION, executionScopeId);
  }

  /**
   * Insert or update key value pairs in the sub scopes under the current scope.
   * Key value pairs directly under the current scope will not be affected.
   *
   * @return {@link JsRecords} representing the updated sub scopes
   */
  public SubScopeUpdater updateSubScopes() {
    return new SubScopeUpdater(table, executionScopeId);
  }

  /**
   * Delete sub scopes under the current scope.
   *
   * @return {@link Void}
   */
  public SubScopeDeleter deleteSubScopes() {
    return new SubScopeDeleter(table, executionScopeId);
  }

}
