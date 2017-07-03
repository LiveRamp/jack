package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

public class ScopeReader extends BaseExecutor2<JsRecord> {

  final Set<String> selectedKeys = Sets.newHashSet();

  ScopeReader(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  public ScopeReader selectKey(String key, String... otherKeys) {
    this.selectedKeys.add(key);
    this.selectedKeys.addAll(Arrays.asList(otherKeys));
    return this;
  }

  public ScopeReader selectKey(Collection<String> keys) {
    selectedKeys.addAll(keys);
    return this;
  }

  @Override
  public JsRecord execute(IDb db) throws IOException {
    Records records = db.createQuery().from(table.table)
        .where(table.scopeColumn.equalTo(executionScopeId))
        .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
        .select(table.typeColumn, table.keyColumn, table.valueColumn)
        .orderBy(table.idColumn)
        .fetch();

    InternalRecordCreator recordCreator = new InternalRecordCreator(table, selectedKeys);
    records.stream().forEach(recordCreator::appendRecord);
    return recordCreator.createNewRecord(executionScopeId);
  }

}
