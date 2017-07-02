package com.rapleaf.jack.store.executors2;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbHelper;
import com.rapleaf.jack.store.json.JsonDbTuple;

abstract class BaseInquirerExecutor2<T, E extends BaseInquirerExecutor2<T, E>> extends BaseExecutor2<T> {

  private final Set<String> selectedKeys;

  BaseInquirerExecutor2(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
    this.selectedKeys = Sets.newHashSet();
  }

  public E selectKey(String key, String... otherKeys) {
    this.selectedKeys.add(key);
    this.selectedKeys.addAll(Arrays.asList(otherKeys));
    return getSelf();
  }

  public E selectKey(Collection<String> keys) {
    selectedKeys.addAll(keys);
    return getSelf();
  }

  abstract E getSelf();

  void appendJsonRecord(Map<String, ValueType> types, Map<String, Object> values, List<JsonDbTuple> jsonTuples, Set<String> jsonKeys) {
    JsonObject jsonObject = JsonDbHelper.fromTupleList(jsonTuples);
    for (String jsonKey : jsonKeys) {
      JsonObject json = jsonObject.get(jsonKey).getAsJsonObject();
      types.put(jsonKey, ValueType.JSON_STRING);
      values.put(jsonKey, json);
    }
  }

  void appendRecord(Map<String, ValueType> types, Map<String, Object> values, List<JsonDbTuple> jsonTuples, Set<String> jsonKeys, Record record) {
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
