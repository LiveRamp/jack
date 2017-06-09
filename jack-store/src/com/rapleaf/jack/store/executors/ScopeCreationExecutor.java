package com.rapleaf.jack.store.executors;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.transaction.ITransactor;

public class ScopeCreationExecutor<DB extends IDb> extends BaseExecutor<DB> {

  private final String newScope;

  ScopeCreationExecutor(ITransactor<DB> transactor, JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames, String newScope) {
    super(transactor, table, predefinedScope, predefinedScopeNames);
    this.newScope = newScope;
  }

  public JsScope execute() {
    JsScope executionScope = predefinedScope.orElseGet(() -> getOrCreateScope(JsConstants.ROOT_SCOPE, predefinedScopeNames));
    return getOrCreateScope(executionScope, Collections.singletonList(newScope));
  }

}
