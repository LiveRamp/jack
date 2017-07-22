package com.rapleaf.jack.store.executors;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.exceptions.InvalidRecordException;

abstract class BaseExecutor<TF, TL> {

  protected final JsTable table;
  protected final Long executionRecordId;

  BaseExecutor(JsTable table, Long executionRecordId) {
    this.table = table;
    this.executionRecordId = executionRecordId;
  }

  public final TF execute(IDb db) throws IOException {
    if (executionRecordId != null) {
      validateExecutionScope(db);
    }
    return internalExecute(db);
  }

  public final TL exec(IDb db) throws IOException {
    if (executionRecordId != null) {
      validateExecutionScope(db);
    }
    return internalExec(db);
  }

  abstract TF internalExecute(IDb db) throws IOException;

  abstract TL internalExec(IDb db) throws IOException;

  private void validateExecutionScope(IDb db) throws IOException {
    Records records = db.createQuery().from(table.table).where(table.id.equalTo(executionRecordId)).fetch();
    if (records.size() != 1) {
      throw new InvalidRecordException("Scope " + executionRecordId + " does not exist");
    }
  }

}
