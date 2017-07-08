package com.rapleaf.jack.store.executors;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

public class RecordReader extends BaseInquirerExecutor<JsRecord, RecordReader> {

  RecordReader(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
  }

  @Override
  JsRecord internalExecute(IDb db) throws IOException {
    Records records = db.createQuery().from(table.table)
        .where(table.scopeColumn.equalTo(executionRecordId))
        .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
        .select(table.typeColumn, table.keyColumn, table.valueColumn)
        .orderBy(table.keyColumn)
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
