package com.rapleaf.jack.store.executors;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

public class RecordReader extends BaseInquirerExecutor<JsRecord, JsRecord, RecordReader> {

  RecordReader(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
  }

  @Override
  JsRecord internalExecute(IDb db) throws IOException {
    return internalExec(db);
  }

  @Override
  JsRecord internalExec(IDb db) throws IOException {
    Records records = db.createQuery().from(table.table)
        .where(table.scope.equalTo(executionRecordId))
        .where(table.type.notEqualTo(ValueType.SCOPE.value))
        .select(table.type, table.key, table.value)
        // records must be sorted by ID so that json entries are read back in the exact same order as they are written to db
        .orderBy(table.id)
        .fetch();

    InternalRecordCreator recordCreator = new InternalRecordCreator(table, selectedKeys);
    records.stream().forEach(recordCreator::appendRecord);
    return recordCreator.createNewRecord(executionRecordId);
  }

  @Override
  RecordReader getSelf() {
    return this;
  }

}
