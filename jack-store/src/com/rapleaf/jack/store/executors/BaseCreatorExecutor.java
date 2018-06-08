package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.iface.ValueIndexer;
import com.rapleaf.jack.store.json.ElementPath;
import com.rapleaf.jack.store.json.JsonDbConstants;
import com.rapleaf.jack.store.json.JsonDbHelper;
import com.rapleaf.jack.store.json.JsonDbTuple;

abstract class BaseCreatorExecutor<TF, TL, E extends BaseCreatorExecutor<TF, TL, E>> extends BaseExecutor<TF, TL> implements ValueIndexer<E> {

  final Map<String, ValueType> types;
  final Map<String, Object> values;

  BaseCreatorExecutor(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
    this.types = Maps.newLinkedHashMap();
    this.values = Maps.newLinkedHashMap();
  }

  abstract E getSelf();

  @Override
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
    if (value instanceof LocalDateTime) {
      return putDateTime(key, (LocalDateTime)value);
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

  @Override
  public E putBoolean(String key, Boolean value) {
    types.put(key, ValueType.BOOLEAN);
    values.put(key, value);
    return getSelf();
  }

  @Override
  public E putInt(String key, Integer value) {
    types.put(key, ValueType.INT);
    values.put(key, value);
    return getSelf();
  }

  @Override
  public E putLong(String key, Long value) {
    types.put(key, ValueType.LONG);
    values.put(key, value);
    return getSelf();
  }

  @Override
  public E putDouble(String key, Double value) {
    types.put(key, ValueType.DOUBLE);
    values.put(key, value);
    return getSelf();
  }

  @Override
  public E putDateTime(String key, LocalDateTime value) {
    types.put(key, ValueType.DATETIME);
    values.put(key, value);
    return getSelf();
  }

  @Override
  public E putString(String key, String value) {
    types.put(key, ValueType.STRING);
    values.put(key, value);
    return getSelf();
  }

  @Override
  public E putJson(String key, JsonObject json) {
    Preconditions.checkNotNull(json);
    types.put(key, ValueType.JSON_STRING);
    values.put(key, json);
    return getSelf();
  }

  @Override
  @SuppressWarnings("unchecked")
  public E putList(String key, List valueList) {
    Preconditions.checkArgument(valueList != null && !valueList.isEmpty(), "Value list cannot be null or empty when using the putList method");

    Object value = valueList.get(0);
    if (value instanceof Boolean) {
      return (E)putBooleanList(key, valueList);
    }
    if (value instanceof Integer) {
      return (E)putIntList(key, valueList);
    }
    if (value instanceof Long) {
      return (E)putLongList(key, valueList);
    }
    if (value instanceof Double) {
      return (E)putDoubleList(key, valueList);
    }
    if (value instanceof LocalDateTime) {
      return (E)putDateTimeList(key, valueList);
    }
    if (value instanceof String) {
      return (E)putStringList(key, valueList);
    }
    throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getSimpleName());
  }

  @Override
  public E putBooleanList(String key, List<Boolean> valueList) {
    types.put(key, ValueType.BOOLEAN_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  @Override
  public E putIntList(String key, List<Integer> valueList) {
    types.put(key, ValueType.INT_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  @Override
  public E putLongList(String key, List<Long> valueList) {
    types.put(key, ValueType.LONG_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  @Override
  public E putDoubleList(String key, List<Double> valueList) {
    types.put(key, ValueType.DOUBLE_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  @Override
  public E putDateTimeList(String key, List<LocalDateTime> valueList) {
    types.put(key, ValueType.DATETIME_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return getSelf();
  }

  @Override
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

  void deleteExistingEntries(IDb db, Long recordId) throws IOException {
    for (String key : types.keySet()) {
      List<Long> rowIdsToDelete = db.createQuery()
          .from(table.table)
          .where(table.scope.equalTo(recordId))
          .where(table.type.notEqualTo(ValueType.SCOPE.value))
          .where(table.key.equalTo(key).or(table.key.startsWith(key + JsonDbConstants.PATH_SEPARATOR)))
          .select(table.id)
          .fetch()
          .gets(table.id);
      db.createDeletion()
          .from(table.table)
          .where(table.id.in(rowIdsToDelete))
          .execute();
    }
  }

  void insertNewEntries(IDb db, Long recordId) throws IOException {
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
        .set(table.scope, Collections.nCopies(typesToInsert.size(), recordId))
        .set(table.type, typesToInsert)
        .set(table.key, keysToInsert)
        .set(table.value, valuesToInsert)
        .execute();
  }

}
