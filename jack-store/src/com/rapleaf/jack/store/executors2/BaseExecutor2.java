package com.rapleaf.jack.store.executors2;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.exceptions.InvalidScopeException;

abstract class BaseExecutor2<T> {

  protected final JsTable table;
  protected final Long executionScopeId;

  BaseExecutor2(JsTable table, Long executionScopeId) {
    this.table = table;
    this.executionScopeId = executionScopeId;
  }

  public final T execute(IDb db) throws IOException {
    if (executionScopeId != null) {
      validateExecutionScope(db);
    }
    return internalExecute(db);
  }

  abstract T internalExecute(IDb db) throws IOException;

  private void validateExecutionScope(IDb db) throws IOException {
    Records records = db.createQuery().from(table.table).where(table.idColumn.equalTo(executionScopeId)).fetch();
    if (records.size() != 1) {
      throw new InvalidScopeException("Scope " + executionScopeId + " does not exist");
    }
  }

}
