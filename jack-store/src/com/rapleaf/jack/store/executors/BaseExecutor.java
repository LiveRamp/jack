package com.rapleaf.jack.store.executors;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.exceptions.InvalidRecordException;

abstract class BaseExecutor<T> {

  protected final JsTable table;
  protected final Long executionRecordId;

  BaseExecutor(JsTable table, Long executionRecordId) {
    this.table = table;
    this.executionRecordId = executionRecordId;
  }

  public final T execute(IDb db) throws IOException {
    if (executionRecordId != null) {
      validateExecutionScope(db);
    }
    return internalExecute(db);
  }

  abstract T internalExecute(IDb db) throws IOException;

  private void validateExecutionScope(IDb db) throws IOException {
    Records records = db.createQuery().from(table.table).where(table.idColumn.equalTo(executionRecordId)).fetch();
    if (records.size() != 1) {
      throw new InvalidRecordException("Scope " + executionRecordId + " does not exist");
    }
  }

}
