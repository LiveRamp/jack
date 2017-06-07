package com.rapleaf.jack.store.executors;

import com.rapleaf.jack.IDb;

public class JsExecutors<DB extends IDb> {

  private final JsBaseExecutor<DB> baseExecutor;

  public JsExecutors(JsBaseExecutor<DB> baseExecutor) {
    this.baseExecutor = baseExecutor;
  }

  ScopeCreationExecutor<DB> createScope(String scope) {
    return new ScopeCreationExecutor<>(baseExecutor, scope);
  }

  ScopeDeletionExecutor<DB> deleteScope(String scope) {
    return new ScopeDeletionExecutor<>(baseExecutor, scope);
  }

  ScopeQueryExecutor<DB> queryScope() {
    return new ScopeQueryExecutor<>(baseExecutor);
  }

}
