package com.rapleaf.jack.store;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.rapleaf.jack.store.executors.JsExecutors;

public class JackStore {

  private final JsTable jsTable;

  public JackStore(JsTable jsTable) {
    this.jsTable = jsTable;
  }

  public JsExecutors scope(String scope, String... moreScopes) {
    List<String> scopes = Lists.newArrayListWithCapacity(1 + moreScopes.length);
    scopes.add(scope);
    scopes.addAll(Arrays.asList(moreScopes));
    return scope(scopes);
  }

  public JsExecutors scope(List<String> scopes) {
    Preconditions.checkArgument(scopes.size() > 0, "Scope list cannot be empty; to specify root scope, please use the `rootScope` method");
    Preconditions.checkArgument(scopes.stream().noneMatch(String::isEmpty), "Scope name cannot be empty");
    return new JsExecutors(jsTable, scopes);
  }

  public JsExecutors scope(JsScope scope) {
    return new JsExecutors(jsTable, scope);
  }

  public JsExecutors rootScope() {
    return scope(JsConstants.ROOT_SCOPE);
  }
}
