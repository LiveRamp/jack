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
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.ElementPath;
import com.rapleaf.jack.store.json.JsonDbHelper;
import com.rapleaf.jack.store.json.JsonDbTuple;

public class ScopeUpdater extends BaseExecutor2<JsRecord> {

  private final Map<String, ValueType> types;
  private final Map<String, Object> values;

  ScopeUpdater(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
    this.types = Maps.newLinkedHashMap();
    this.values = Maps.newLinkedHashMap();
  }

  public ScopeUpdater put(String key, Object value) {
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
    throw new JackRuntimeException("Unsupported value type: " + value.getClass().getSimpleName());
  }

  public ScopeUpdater putBoolean(String key, Boolean value) {
    types.put(key, ValueType.BOOLEAN);
    values.put(key, value);
    return this;
  }

  public ScopeUpdater putInt(String key, Integer value) {
    types.put(key, ValueType.INT);
    values.put(key, value);
    return this;
  }

  public ScopeUpdater putLong(String key, Long value) {
    types.put(key, ValueType.LONG);
    values.put(key, value);
    return this;
  }

  public ScopeUpdater putDouble(String key, Double value) {
    types.put(key, ValueType.DOUBLE);
    values.put(key, value);
    return this;
  }

  public ScopeUpdater putDateTime(String key, DateTime value) {
    types.put(key, ValueType.DATETIME);
    values.put(key, value);
    return this;
  }

  public ScopeUpdater putString(String key, String value) {
    types.put(key, ValueType.STRING);
    values.put(key, value);
    return this;
  }

  public ScopeUpdater putJson(String key, JsonObject json) {
    Preconditions.checkNotNull(json);

    List<JsonDbTuple> tuples = JsonDbHelper.toTupleList(Collections.singletonList(new ElementPath(key)), json);
    for (JsonDbTuple tuple : tuples) {
      types.put(tuple.getFullPaths(), tuple.getType());
      values.put(tuple.getFullPaths(), tuple.getValue());
    }
    return this;
  }

  @SuppressWarnings("unchecked")
  public ScopeUpdater putList(String key, List<Object> valueList) {
    Preconditions.checkArgument(valueList != null && !valueList.isEmpty(), "Value list cannot be null or empty when using the putList method");

    Object value = valueList.get(0);
    if (value instanceof Boolean) {
      return putBooleanList(key, (List)valueList);
    }
    if (value instanceof Integer) {
      return putIntList(key, (List)valueList);
    }
    if (value instanceof Long) {
      return putLongList(key, (List)valueList);
    }
    if (value instanceof Double) {
      return putDoubleList(key, (List)valueList);
    }
    if (value instanceof DateTime) {
      return putDateTimeList(key, (List)valueList);
    }
    if (value instanceof String) {
      return putStringList(key, (List)valueList);
    }
    throw new JackRuntimeException("Unsupported value type: " + value.getClass().getSimpleName());
  }

  public ScopeUpdater putBooleanList(String key, List<Boolean> valueList) {
    types.put(key, ValueType.BOOLEAN_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public ScopeUpdater putIntList(String key, List<Integer> valueList) {
    types.put(key, ValueType.INT_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public ScopeUpdater putLongList(String key, List<Long> valueList) {
    types.put(key, ValueType.LONG_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public ScopeUpdater putDoubleList(String key, List<Double> valueList) {
    types.put(key, ValueType.DOUBLE_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public ScopeUpdater putDateTimeList(String key, List<DateTime> valueList) {
    types.put(key, ValueType.DATETIME_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public ScopeUpdater putStringList(String key, List<String> valueList) {
    types.put(key, ValueType.STRING_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  private List nullifyEmptyList(List valueList) {
    if (valueList != null && valueList.isEmpty()) {
      return null;
    } else {
      return valueList;
    }
  }

  @Override
  public JsRecord execute(IDb db) throws IOException {
    if (!types.isEmpty()) {
      deleteExistingEntries(db);
      insertNewEntries(db);
    }
    return new JsRecord(executionScopeId, types, values);
  }

  private void deleteExistingEntries(IDb db) throws IOException {
    db.createDeletion()
        .from(table.table)
        .where(table.scopeColumn.equalTo(executionScopeId))
        .where(table.keyColumn.in(types.keySet()))
        .execute();
  }

  private void insertNewEntries(IDb db) throws IOException {
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
      } else {
        keysToInsert.add(key);
        typesToInsert.add(type.value);
        valuesToInsert.add(String.valueOf(value));
      }
    }

    db.createInsertion()
        .into(table.table)
        .set(table.scopeColumn, Collections.nCopies(typesToInsert.size(), executionScopeId))
        .set(table.typeColumn, typesToInsert)
        .set(table.keyColumn, keysToInsert)
        .set(table.valueColumn, valuesToInsert)
        .execute();
  }

}
