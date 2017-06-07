package com.rapleaf.jack.store.executors;

import java.util.Collections;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsScope;

public class ScopeCreationExecutor<DB extends IDb> {

  private final JsBaseExecutor<DB> baseExecutor;
  private final String newScope;

  public ScopeCreationExecutor(JsBaseExecutor<DB> baseExecutor, String newScope) {
    this.baseExecutor = baseExecutor;
    this.newScope = newScope;
  }

  public JsScope execute() {
    return baseExecutor.getOrCreateScope(Collections.singletonList(newScope));
  }

}
