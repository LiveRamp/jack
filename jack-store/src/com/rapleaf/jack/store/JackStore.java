package com.rapleaf.jack.store;

import com.rapleaf.jack.store.executors.JsExecutors;

public class JackStore {

  private final JsTable table;

  public JackStore(JsTable table) {
    this.table = table;
  }

  public JsExecutors scope(Long scopeId) {
    return new JsExecutors(table, scopeId);
  }

  public JsExecutors rootScope() {
    return scope(JsConstants.ROOT_SCOPE_ID);
  }

}
