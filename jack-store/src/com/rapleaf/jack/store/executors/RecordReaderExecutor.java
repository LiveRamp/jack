package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.GenericQuery;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.exceptions.MissingScopeException;
import com.rapleaf.jack.store.json.JsonDbHelper;
import com.rapleaf.jack.store.json.JsonDbTuple;

/**
 * Get records under the execution scope
 */
public class RecordReaderExecutor<DB extends IDb> extends BaseExecutor<DB> {

  private final Set<String> selectedKeys;

  RecordReaderExecutor(JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(table, predefinedScope, predefinedScopeNames);
    this.selectedKeys = Sets.newHashSet();
  }

  public RecordReaderExecutor<DB> selectKey(String key, String... otherKeys) {
    selectedKeys.add(key);
    selectedKeys.addAll(Arrays.asList(otherKeys));
    return this;
  }

  public RecordReaderExecutor<DB> selectKey(Collection<String> keys) {
    selectedKeys.addAll(keys);
    return this;
  }

  public JsRecord execute(DB db) throws IOException {
    JsRecords records = internalGets(db, Collections.emptyList());
    Preconditions.checkState(records.size() <= 1);
    if (records.size() == 0) {
      return JsRecord.empty();
    } else {
      return records.getOnly();
    }
  }

  @SuppressWarnings("unchecked")
  public JsRecords internalGets(DB db, Collection<JsScope> targetScopes) throws IOException {
    final Optional<JsScope> recordScope;
    if (targetScopes.isEmpty()) {
      recordScope = getOptionalExecutionScope(db);
      if (!recordScope.isPresent()) {
        throw new MissingScopeException(Joiner.on("/").join(predefinedScopeNames));
      }
    } else {
      recordScope = Optional.empty();
    }

    GenericQuery query = db.createQuery().from(table.table)
        .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
        .select(table.scopeColumn, table.typeColumn, table.keyColumn, table.valueColumn);

    if (targetScopes.isEmpty()) {
      query.where(table.scopeColumn.equalTo(recordScope.get().getScopeId()));
    } else {
      query.where(table.scopeColumn.in(targetScopes.stream().map(JsScope::getScopeId).collect(Collectors.toList())))
          .orderBy(table.scopeColumn);
    }

    Records records = query.orderBy(table.idColumn).fetch();

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
        Optional<JsRecord> newJsRecord = getJsRecord(types, values, jsonTuples, jsonKeys);
        newJsRecord.ifPresent(jsRecords::add);

        types = Maps.newHashMap();
        values = Maps.newHashMap();
        jsonTuples = Lists.newLinkedList();
        jsonKeys = Sets.newHashSet();

        previousScopeId = scopeId;
      }

      appendRecord(types, values, jsonTuples, jsonKeys, record);

      if (!iterator.hasNext()) {
        // Construct the final record with previous entries
        Optional<JsRecord> newJsRecord = getJsRecord(types, values, jsonTuples, jsonKeys);
        newJsRecord.ifPresent(jsRecords::add);
      }
    }

    return new JsRecords(jsRecords);
  }

  private Optional<JsRecord> getJsRecord(Map<String, ValueType> types, Map<String, Object> values, List<JsonDbTuple> jsonTuples, Set<String> jsonKeys) {
    JsonObject jsonObject = JsonDbHelper.fromTupleList(jsonTuples);
    for (String jsonKey : jsonKeys) {
      JsonObject json = jsonObject.get(jsonKey).getAsJsonObject();
      types.put(jsonKey, ValueType.JSON_STRING);
      values.put(jsonKey, json);
    }
    return types.isEmpty() ? Optional.empty() : Optional.of(new JsRecord(types, values));
  }

  private void appendRecord(Map<String, ValueType> types, Map<String, Object> values, List<JsonDbTuple> jsonTuples, Set<String> jsonKeys, Record record) {
    ValueType type = ValueType.findByValue(record.get(table.typeColumn));
    String key = record.get(table.keyColumn);
    String value = record.get(table.valueColumn);

    switch (type.category) {
      case PRIMITIVE:
        if (!isSelectedKey(key)) {
          break;
        }
        types.put(key, type);
        values.put(key, value);
        break;
      case JSON:
        JsonDbTuple tuple = JsonDbTuple.create(key, type, value);
        String jsonKey = tuple.getPaths().get(0).getName().get();
        if (!isSelectedKey(jsonKey)) {
          break;
        }
        jsonKeys.add(jsonKey);
        jsonTuples.add(tuple);
        break;
      case LIST:
        if (!isSelectedKey(key)) {
          break;
        }
        types.put(key, type);
        if (!values.containsKey(key)) {
          values.put(key, Lists.newArrayList());
        }
        if (value != null) {
          ((List<Object>)values.get(key)).add(value);
        }
        break;
      default:
        throw new IllegalStateException("Unexpected type: " + type.name());
    }
  }

  private boolean isSelectedKey(String key) {
    return selectedKeys.isEmpty() || selectedKeys.contains(key);
  }

}
