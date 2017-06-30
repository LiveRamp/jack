package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.exceptions.MissingScopeException;
import com.rapleaf.jack.store.json.JsonDbTuple;

/**
 * Get records under the execution scope
 */
public class RecordReaderExecutor<DB extends IDb> extends BaseReaderExecutor<DB, RecordReaderExecutor<DB>> {

  RecordReaderExecutor(JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(table, predefinedScope, predefinedScopeNames);
  }

  public JsRecord execute(DB db) throws IOException {
    Optional<JsScope> recordScope = getOptionalExecutionScope(db);
    if (!recordScope.isPresent()) {
      throw new MissingScopeException(Joiner.on("/").join(predefinedScopeNames));
    }

    Records records = db.createQuery().from(table.table)
        .where(table.scopeColumn.equalTo(recordScope.get().getScopeId()))
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

    if (types.isEmpty()) {
      return JsRecord.empty();
    } else {
      return new JsRecord(types, values);
    }
  }

  @Override
  RecordReaderExecutor<DB> getSelf() {
    return this;
  }

}
