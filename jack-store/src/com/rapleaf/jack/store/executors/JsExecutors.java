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

}
