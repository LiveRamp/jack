package com.rapleaf.jack.store.executors2;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbHelper;
import com.rapleaf.jack.store.json.JsonDbTuple;

final class InternalRecordCreator {

  private final JsTable table;
  private final Set<String> selectedKeys;

  private final Map<String, ValueType> types = Maps.newHashMap();
  private final Map<String, Object> values = Maps.newHashMap();
  private final List<JsonDbTuple> jsonTuples = Lists.newLinkedList();
  private final Set<String> jsonKeys = Sets.newHashSet();

  InternalRecordCreator(JsTable table, Set<String> selectedKeys) {
    this.table = table;
    this.selectedKeys = selectedKeys;
  }

  boolean hasNewRecord() {
    return !types.isEmpty() || !jsonKeys.isEmpty();
  }

  JsRecord createNewRecord(Long scopeId) {
    JsonObject jsonObject = JsonDbHelper.fromTupleList(jsonTuples);
    for (String jsonKey : jsonKeys) {
      JsonObject json = jsonObject.get(jsonKey).getAsJsonObject();
      types.put(jsonKey, ValueType.JSON_STRING);
      values.put(jsonKey, json);
    }
    JsRecord record = new JsRecord(scopeId, types, values);
    clear();
    return record;
  }

  void appendRecord(Record record) {
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

  private void clear() {
    types.clear();
    values.clear();
    jsonKeys.clear();
    jsonTuples.clear();
  }

}
