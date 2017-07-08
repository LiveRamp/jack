package com.rapleaf.jack.store;

import com.rapleaf.jack.store.executors2.JsExecutors2;

public class JackStore2 {

  private final JsTable table;

  public JackStore2(JsTable table) {
    this.table = table;
  }

  public JsExecutors2 scope(Long scopeId) {
    return new JsExecutors2(table, scopeId);
  }

  public JsExecutors2 rootScope() {
    return scope(JsConstants.ROOT_SCOPE_ID);
  }

}
