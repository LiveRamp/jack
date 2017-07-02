package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbTuple;

public class ScopeReader extends BaseInquirerExecutor2<JsRecord, ScopeReader> {

  ScopeReader(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  @Override
  ScopeReader getSelf() {
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

    Map<String, ValueType> types = Maps.newHashMap();
    Map<String, Object> values = Maps.newHashMap();
    List<JsonDbTuple> jsonTuples = Lists.newLinkedList();
    Set<String> jsonKeys = Sets.newHashSet();

    for (Record record : records) {
      appendRecord(types, values, jsonTuples, jsonKeys, record);
    }
    appendJsonRecord(types, values, jsonTuples, jsonKeys);

    return new JsRecord(executionScopeId, types, values);
  }

}
