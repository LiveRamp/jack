package com.rapleaf.jack.store.executors;

import java.util.Collections;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsScope;

public class ScopeCreationExecutor<DB extends IDb> {

  private final JsBaseExecutor<DB> baseExecutor;
  private final JsScope executionScope;
  private final String newScope;

  public ScopeCreationExecutor(JsBaseExecutor<DB> baseExecutor, JsScope executionScope, String newScope) {
    this.baseExecutor = baseExecutor;
    this.executionScope = executionScope;
    this.newScope = newScope;
  }

  public JsScope execute() {
    return baseExecutor.getOrCreateScope(executionScope, Collections.singletonList(newScope));
  }

}
