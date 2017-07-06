package com.rapleaf.jack.store.executors2;

import java.io.IOException;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

public class ScopeReader extends BaseInquirerExecutor2<JsRecord, ScopeReader> {

  ScopeReader(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  @Override
  public JsRecord execute(IDb db) throws IOException {
    Records records = db.createQuery().from(table.table)
        .where(table.scopeColumn.equalTo(executionScopeId))
        .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
        .select(table.typeColumn, table.keyColumn, table.valueColumn)
        .orderBy(table.keyColumn)
        .fetch();

    InternalRecordCreator recordCreator = new InternalRecordCreator(table, selectedKeys);
    records.stream().forEach(recordCreator::appendRecord);
    return recordCreator.createNewRecord(executionScopeId);
  }

  @Override
  ScopeReader getSelf() {
    return this;
  }

}
