package com.rapleaf.jack.store.executors;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsScope;

public class JsExecutors<DB extends IDb> {

  private final JsBaseExecutor<DB> baseExecutor;
  private final JsScope executionScope;

  public JsExecutors(JsBaseExecutor<DB> baseExecutor, JsScope executionScope) {
    this.baseExecutor = baseExecutor;
    this.executionScope = executionScope;
  }

  ScopeCreationExecutor<DB> createScope(String scope) {
    return new ScopeCreationExecutor<>(baseExecutor, executionScope, scope);
  }

  ScopeRenameExecutor<DB> renameScope(String currentName, String newName) {
    return new ScopeRenameExecutor<>(baseExecutor, executionScope, currentName, newName);
  }

  ScopeQueryExecutor<DB> queryScope() {
    return new ScopeQueryExecutor<>(baseExecutor, executionScope);
  }

  ScopeQueryExecutor<DB> deleteScope() {
    return new ScopeQueryExecutor<>(baseExecutor, executionScope);
  }

}
