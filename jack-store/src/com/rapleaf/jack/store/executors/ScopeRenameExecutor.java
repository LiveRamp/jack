package com.rapleaf.jack.store.executors;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsScope;

public class ScopeRenameExecutor<DB extends IDb> {

  private final JsBaseExecutor<DB> baseExecutor;
  private final JsScope executionScope;
  private final String currentName;
  private final String newName;

  public ScopeRenameExecutor(JsBaseExecutor<DB> baseExecutor, JsScope executionScope, String currentName, String newName) {
    this.baseExecutor = baseExecutor;
    this.executionScope = executionScope;
    this.currentName = currentName;
    this.newName = newName;
  }

  public boolean execute() {
    return baseExecutor.renameScope(executionScope, currentName, newName);
  }

}
