package com.rapleaf.jack.store.executors;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.transaction.ITransactor;

public class ScopeCreationExecutor<DB extends IDb> extends BaseExecutor<DB> {

  private final Optional<String> newScope;

  ScopeCreationExecutor(ITransactor<DB> transactor, JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames, String newScope) {
    super(transactor, table, predefinedScope, predefinedScopeNames);
    this.newScope = Optional.of(newScope);
  }

  ScopeCreationExecutor(ITransactor<DB> transactor, JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(transactor, table, predefinedScope, predefinedScopeNames);
    this.newScope = Optional.empty();
  }

  public JsScope execute() {
    String scopeName = newScope.orElseGet(() -> UUID.randomUUID().toString());
    return getOrCreateScope(getOrCreateExecutionScope(), Collections.singletonList(scopeName));
  }

}
