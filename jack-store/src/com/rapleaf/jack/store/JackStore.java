package com.rapleaf.jack.store;

import com.rapleaf.jack.store.executors.JsExecutors;

public class JackStore {

  private final JsTable table;

  public JackStore(JsTable table) {
    this.table = table;
  }

  public JsExecutors record(Long recordId) {
    return new JsExecutors(table, recordId);
  }

  public JsExecutors rootRecord() {
    return record(JsConstants.ROOT_RECORD_ID);
  }

}
