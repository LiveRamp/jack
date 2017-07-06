package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.ElementPath;
import com.rapleaf.jack.store.json.JsonDbConstants;
import com.rapleaf.jack.store.json.JsonDbHelper;
import com.rapleaf.jack.store.json.JsonDbTuple;

abstract class BaseCreatorExecutor2<T, E extends BaseCreatorExecutor2<T, E>> extends BaseExecutor2<T> {

  final Map<String, ValueType> types;
  final Map<String, Object> values;

  BaseCreatorExecutor2(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
    this.types = Maps.newLinkedHashMap();
    this.values = Maps.newLinkedHashMap();
  }

  abstract E getSelf();

  public E put(String key, Object value) {
    Preconditions.checkNotNull(value, "Value cannot be null when using the put(String, Object) method");
    if (value instanceof Boolean) {
      return putBoolean(key, (Boolean)value);
    }
    if (value instanceof Integer) {
      return putInt(key, (Integer)value);
    }
    if (value instanceof Long) {
      return putLong(key, (Long)value);
    }
    if (value instanceof Double) {
      return putDouble(key, (Double)value);
    }
    if (value instanceof DateTime) {
      return putDateTime(key, (DateTime)value);
    }
    if (value instanceof String) {
      return putString(key, (String)value);
    }
    if (value instanceof JsonObject) {
      return putJson(key, (JsonObject)value);
    }
    if (value instanceof List) {
      return putList(key, (List<Object>)value);
    }
    throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getSimpleName());
  }

  public E putBoolean(String key, Boolean value) {
    types.put(key, ValueType.BOOLEAN);
    values.put(key, value);
    return getSelf();
  }

  public E putInt(String key, Integer value) {
    types.put(key, ValueType.INT);
    values.put(key, value);
    return getSelf();
  }

  public E putLong(String key, Long value) {
    types.put(key, ValueType.LONG);
    values.put(key, value);
    return getSelf();
  }

  public E putDouble(String key, Double value) {
    types.put(key, ValueType.DOUBLE);
    values.put(key, value);
    return getSelf();
  }

  public E putDateTime(String key, DateTime value) {
    types.put(key, ValueType.DATETIME);
    values.put(key, value);
    return getSelf();
  }

  public E putString(String key, String value) {
    types.put(key, ValueType.STRING);
    values.put(key, value);
    return getSelf();
  }

  public E putJson(String key, JsonObject json) {
    Preconditions.checkNotNull(json);
    types.put(key, ValueType.JSON_STRING);
    values.put(key, json);
    return getSelf();
  }

  @SuppressWarnings("unchecked")
  public E putList(String key, List<Object> valueList) {
    Preconditions.checkArgument(valueList != null && !valueList.isEmpty(), "Value list cannot be null or empty when using the putList method");

    Object value = valueList.get(0);
    if (value instanceof Boolean) {
      return (E)putBooleanList(key, (List)valueList);
    }
    if (value instanceof Integer) {
      return (E)putIntList(key, (List)valueList);
    }
    if (value instanceof Long) {
      return (E)putLongList(key, (List)valueList);
    }
    if (value instanceof Double) {
      return (E)putDoubleList(key, (List)valueList);
    }
    if (value instanceof DateTime) {
      return (E)putDateTimeList(key, (List)valueList);
    }
    if (value instanceof String) {
      return (E)putStringList(key, (List)valueList);
    }
    throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getSimpleName());
  }

  public E putBooleanList(String key, List<Boolean> valueList) {
    types.put(key, ValueType.BOOLEAN_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  public E putIntList(String key, List<Integer> valueList) {
    types.put(key, ValueType.INT_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  public E putLongList(String key, List<Long> valueList) {
    types.put(key, ValueType.LONG_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  public E putDoubleList(String key, List<Double> valueList) {
    types.put(key, ValueType.DOUBLE_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  public E putDateTimeList(String key, List<DateTime> valueList) {
    types.put(key, ValueType.DATETIME_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  public E putStringList(String key, List<String> valueList) {
    types.put(key, ValueType.STRING_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  private List nullifyEmptyList(List valueList) {
    if (valueList != null && valueList.isEmpty()) {
      return null;
    } else {
      return valueList;
    }
  }

  void deleteExistingEntries(IDb db, Long scopeId) throws IOException {
    for (String key : types.keySet()) {
      db.createDeletion()
          .from(table.table)
          .where(table.scopeColumn.equalTo(scopeId))
          .where(table.keyColumn.equalTo(key).or(table.keyColumn.startsWith(key + JsonDbConstants.PATH_SEPARATOR)))
          .execute();
    }
  }

  void insertNewEntries(IDb db, Long scopeId) throws IOException {
    List<Integer> typesToInsert = Lists.newLinkedList();
    List<String> keysToInsert = Lists.newLinkedList();
    List<String> valuesToInsert = Lists.newLinkedList();

    for (Map.Entry<String, ValueType> entry : types.entrySet()) {
      String key = entry.getKey();
      ValueType type = entry.getValue();
      Object value = values.get(key);

      if (value == null) {
        keysToInsert.add(key);
        typesToInsert.add(type.value);
        valuesToInsert.add(null);
      } else if (type.isList()) {
        for (Object v : (List)value) {
          keysToInsert.add(key);
          typesToInsert.add(type.value);
          valuesToInsert.add(String.valueOf(v));
        }
      } else if (type.isJson()) {
        List<JsonDbTuple> tuples = JsonDbHelper.toTupleList(Collections.singletonList(new ElementPath(key)), (JsonObject)value);
        for (JsonDbTuple tuple : tuples) {
          keysToInsert.add(tuple.getFullPaths());
          typesToInsert.add(tuple.getType().value);
          valuesToInsert.add(tuple.getValue());
        }
      } else {
        keysToInsert.add(key);
        typesToInsert.add(type.value);
        valuesToInsert.add(String.valueOf(value));
      }
    }

    db.createInsertion()
        .into(table.table)
        .set(table.scopeColumn, Collections.nCopies(typesToInsert.size(), scopeId))
        .set(table.typeColumn, typesToInsert)
        .set(table.keyColumn, keysToInsert)
        .set(table.valueColumn, valuesToInsert)
        .execute();
  }

}
