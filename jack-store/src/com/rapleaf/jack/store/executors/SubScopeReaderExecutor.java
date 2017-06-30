package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbTuple;

/**
 * Read sub scope records under the execution scope
 */
public class SubScopeReaderExecutor<DB extends IDb> extends BaseReaderExecutor<DB, SubScopeReaderExecutor<DB>> {

  private final Set<Long> subScopeIds;

  SubScopeReaderExecutor(JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames, Set<Long> subScopeIds) {
    super(table, predefinedScope, predefinedScopeNames);
    this.subScopeIds = subScopeIds;
  }

  public JsRecords execute(DB db) throws IOException {
    Records records = db.createQuery().from(table.table)
        .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
        .where(table.scopeColumn.in(subScopeIds))
        .select(table.scopeColumn, table.typeColumn, table.keyColumn, table.valueColumn)
        .orderBy(table.scopeColumn)
        .orderBy(table.idColumn).fetch();

    if (records.isEmpty()) {
      return JsRecords.empty();
    }

    List<JsRecord> jsRecords = Lists.newLinkedList();

    Long previousScopeId = records.get(0).get(table.scopeColumn);
    Map<String, ValueType> types = Maps.newHashMap();
    Map<String, Object> values = Maps.newHashMap();
    List<JsonDbTuple> jsonTuples = Lists.newLinkedList();
    Set<String> jsonKeys = Sets.newHashSet();

    Iterator<Record> iterator = records.iterator();
    while (iterator.hasNext()) {
      Record record = iterator.next();
      Long scopeId = record.get(table.scopeColumn);

      if (!Objects.equals(previousScopeId, scopeId)) {
        // Scope ID changes
        // Construct a new record with previous entries
        addJsRecord(types, values, jsonTuples, jsonKeys, jsRecords);

        types = Maps.newHashMap();
        values = Maps.newHashMap();
        jsonTuples = Lists.newLinkedList();
        jsonKeys = Sets.newHashSet();

        previousScopeId = scopeId;
      }

      appendRecord(types, values, jsonTuples, jsonKeys, record);

      if (!iterator.hasNext()) {
        // Construct the final record with previous entries
        addJsRecord(types, values, jsonTuples, jsonKeys, jsRecords);
      }
    }

    return new JsRecords(jsRecords);
  }

  private void addJsRecord(Map<String, ValueType> types, Map<String, Object> values, List<JsonDbTuple> jsonTuples, Set<String> jsonKeys, List<JsRecord> jsRecords) {
    appendJsonRecord(types, values, jsonTuples, jsonKeys);
    if (!types.isEmpty()) {
      jsRecords.add(new JsRecord(types, values));
    }
  }

  @Override
  SubScopeReaderExecutor<DB> getSelf() {
    return this;
  }

}
