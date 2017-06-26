package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.ElementPath;
import com.rapleaf.jack.store.json.JsonDbHelper;
import com.rapleaf.jack.store.json.JsonDbTuple;
import com.rapleaf.jack.transaction.ITransactor;

public class RecordIndexExecutor<DB extends IDb> extends BaseExecutor<DB> {

  private final Map<String, ValueType> types;
  private final Map<String, Object> values;

  protected RecordIndexExecutor(ITransactor<DB> transactor, JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(transactor, table, predefinedScope, predefinedScopeNames);
    this.types = Maps.newLinkedHashMap();
    this.values = Maps.newLinkedHashMap();
  }

  public RecordIndexExecutor<DB> put(String key, Object value) {
    Preconditions.checkNotNull(value, "Value list cannot be null when using the put(String, Object) method");
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

  public RecordIndexExecutor<DB> putBoolean(String key, Boolean value) {
    types.put(key, ValueType.BOOLEAN);
    values.put(key, value);
    return this;
  }

  public RecordIndexExecutor<DB> putInt(String key, Integer value) {
    types.put(key, ValueType.INT);
    values.put(key, value);
    return this;
  }

  public RecordIndexExecutor<DB> putLong(String key, Long value) {
    types.put(key, ValueType.LONG);
    values.put(key, value);
    return this;
  }

  public RecordIndexExecutor<DB> putDouble(String key, Double value) {
    types.put(key, ValueType.DOUBLE);
    values.put(key, value);
    return this;
  }

  public RecordIndexExecutor<DB> putDateTime(String key, DateTime value) {
    types.put(key, ValueType.DATETIME);
    values.put(key, value);
    return this;
  }

  public RecordIndexExecutor<DB> putString(String key, String value) {
    types.put(key, ValueType.STRING);
    values.put(key, value);
    return this;
  }

  public RecordIndexExecutor<DB> putJson(String key, JsonObject json) {
    Preconditions.checkNotNull(json);

    List<JsonDbTuple> tuples = JsonDbHelper.toTupleList(Collections.singletonList(new ElementPath(key)), json);
    for (JsonDbTuple tuple : tuples) {
      types.put(tuple.getFullPaths(), tuple.getType());
      values.put(tuple.getFullPaths(), tuple.getValue());
    }
    return this;
  }

  @SuppressWarnings("unchecked")
  public RecordIndexExecutor<DB> putList(String key, List<Object> valueList) {
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

  public RecordIndexExecutor<DB> putBooleanList(String key, List<Boolean> valueList) {
    types.put(key, ValueType.BOOLEAN_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public RecordIndexExecutor<DB> putIntList(String key, List<Integer> valueList) {
    types.put(key, ValueType.INT_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public RecordIndexExecutor<DB> putLongList(String key, List<Long> valueList) {
    types.put(key, ValueType.LONG_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public RecordIndexExecutor<DB> putDoubleList(String key, List<Double> valueList) {
    types.put(key, ValueType.DOUBLE_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public RecordIndexExecutor<DB> putDateTimeList(String key, List<DateTime> valueList) {
    types.put(key, ValueType.DATETIME_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public RecordIndexExecutor<DB> putStringList(String key, List<String> valueList) {
    types.put(key, ValueType.STRING_LIST);
    values.put(key, nullifyEmptyList(valueList));
    return this;
  }

  public void execute() {
    Long scopeId = getOrCreateExecutionScope().getScopeId();
    transactor.executeAsTransaction(db -> {
      Map<String, List<Long>> existingKeyIds = getExistingKeyIdMap(db, scopeId);

      Set<String> existingKeys = existingKeyIds.keySet();
      if (!existingKeys.isEmpty()) {
        updateExistingKeys(db, scopeId, Lists.newLinkedList(existingKeys));
      }

      Set<String> newKeys = Sets.difference(types.keySet(), existingKeys);
      if (!newKeys.isEmpty()) {
        insertNewKeys(db, scopeId, Lists.newLinkedList(newKeys));
      }
    });
  }

  private List nullifyEmptyList(List valueList) {
    if (valueList != null && valueList.isEmpty()) {
      return null;
    } else {
      return valueList;
    }
  }

  private Map<String, List<Long>> getExistingKeyIdMap(IDb db, Long scopeId) throws IOException {
    Map<String, List<Long>> keyIdMap = Maps.newLinkedHashMap();

    Records records = db.createQuery()
        .from(table.table)
        .where(table.scopeColumn.as(Long.class).equalTo(scopeId))
        .where(table.keyColumn.in(types.keySet()))
        .select(table.idColumn, table.keyColumn)
        .orderBy(table.idColumn)
        .fetch();

    for (Record record : records) {
      long id = record.get(table.idColumn);
      String key = record.get(table.keyColumn);
      if (keyIdMap.containsKey(key)) {
        keyIdMap.get(key).add(id);
      } else {
        keyIdMap.put(key, Lists.newArrayList(id));
      }
    }

    return keyIdMap;
  }

  private void insertNewKeys(DB db, Long scopeId, List<String> newKeys) throws IOException {
    List<String> typesToInsert = Lists.newLinkedList();
    List<String> keysToInsert = Lists.newLinkedList();
    List<String> valuesToInsert = Lists.newLinkedList();

    for (String key : newKeys) {
      ValueType type = types.get(key);
      Object value = values.get(key);

      if (value == null) {
        keysToInsert.add(key);
        typesToInsert.add(type.name());
        valuesToInsert.add(null);
      } else if (type.isList()) {
        for (Object v : (List)value) {
          keysToInsert.add(key);
          typesToInsert.add(type.name());
          valuesToInsert.add(String.valueOf(v));
        }
      } else {
        keysToInsert.add(key);
        typesToInsert.add(type.name());
        valuesToInsert.add(String.valueOf(value));
      }
    }

    db.createInsertion()
        .into(table.table)
        .set(table.scopeColumn.as(Long.class), Collections.nCopies(typesToInsert.size(), scopeId))
        .set(table.typeColumn, typesToInsert)
        .set(table.keyColumn, keysToInsert)
        .set(table.valueColumn, valuesToInsert)
        .execute();
  }

  // delete existing keys and insert new values
  // replace with true update in the future
  private void updateExistingKeys(DB db, Long scopeId, List<String> existingKeys) throws IOException {
    db.createDeletion()
        .from(table.table)
        .where(table.scopeColumn.as(Long.class).equalTo(scopeId))
        .where(table.keyColumn.in(existingKeys))
        .execute();

    insertNewKeys(db, scopeId, existingKeys);
  }

}
