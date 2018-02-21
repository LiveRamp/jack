package com.rapleaf.jack.store;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;

import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.iface.ValueContainer;

public class JsRecord implements ValueContainer<JsRecord> {

  private final Long recordId;
  private final Map<String, ValueType> types;
  private final Map<String, Object> values;

  public JsRecord(Long recordId, Map<String, ValueType> types, Map<String, Object> values) {
    Preconditions.checkNotNull(types);
    Preconditions.checkNotNull(values);
    Preconditions.checkArgument(types.keySet().equals(values.keySet()));
    this.recordId = recordId;
    this.types = types;
    this.values = values;
  }

  public static JsRecord empty(Long recordId) {
    return new JsRecord(recordId, Collections.emptyMap(), Collections.emptyMap());
  }

  public Long getRecordId() {
    return recordId;
  }

  public Set<String> keySet() {
    return types.keySet();
  }

  public boolean isEmpty() {
    return types.isEmpty();
  }

  public Map<String, ValueType> getKeyTypes() {
    return types;
  }

  public ValueType getKeyType(String key) {
    checkKey(key);
    return types.get(key);
  }

  @Override
  public Object get(String key) {
    checkKey(key);

    ValueType type = types.get(key);
    switch (type) {
      case BOOLEAN:
        return checkTypeAndGetNullable(key, ValueType.BOOLEAN, Boolean::valueOf);
      case INT:
        return checkTypeAndGetNullable(key, ValueType.INT, Integer::valueOf);
      case LONG:
        return checkTypeAndGetNullable(key, ValueType.LONG, Long::valueOf);
      case DOUBLE:
        return checkTypeAndGetNullable(key, ValueType.DOUBLE, Double::valueOf);
      case DATETIME:
        return checkTypeAndGetNullable(key, ValueType.DATETIME, DateTime::parse);
      case STRING:
        return checkTypeAndGetNullable(key, ValueType.STRING, Function.identity());
      default:
        if (type.category == ValueType.Category.JSON) {
          return checkAndGetJson(key);
        }
        throw new JackRuntimeException("Unsupported value type: " + type.name());
    }
  }

  @Override
  public Boolean getBoolean(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.BOOLEAN, Boolean::valueOf);
  }

  @Override
  public Integer getInt(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.INT, Integer::valueOf);
  }

  @Override
  public Long getLong(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.LONG, Long::valueOf);
  }

  @Override
  public Double getDouble(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.DOUBLE, Double::valueOf);
  }

  @Override
  public DateTime getDateTime(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.DATETIME, DateTime::parse);
  }

  @Override
  public String getString(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.STRING, Function.identity());
  }

  @Override
  public JsonObject getJson(String key) {
    checkKey(key);
    return checkAndGetJson(key);
  }

  @Override
  public List getList(String key) {
    checkKey(key);

    ValueType type = types.get(key);
    Preconditions.checkArgument(type.isList(), "Key " + key + " has type " + type.name() + " and is not a list");

    switch (type) {
      case BOOLEAN_LIST:
        return checkTypeAndGetList(key, ValueType.BOOLEAN_LIST, Boolean::valueOf);
      case INT_LIST:
        return checkTypeAndGetList(key, ValueType.INT_LIST, Integer::valueOf);
      case LONG_LIST:
        return checkTypeAndGetList(key, ValueType.LONG_LIST, Long::valueOf);
      case DOUBLE_LIST:
        return checkTypeAndGetList(key, ValueType.DOUBLE_LIST, Double::valueOf);
      case DATETIME_LIST:
        return checkTypeAndGetList(key, ValueType.DATETIME_LIST, DateTime::parse);
      case STRING_LIST:
        return checkTypeAndGetList(key, ValueType.STRING_LIST, Function.identity());
      default:
        throw new JackRuntimeException("Unsupported value type: " + type.name());
    }
  }

  @Override
  public List<Boolean> getBooleanList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.BOOLEAN_LIST, Boolean::valueOf);
  }

  @Override
  public List<Integer> getIntList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.INT_LIST, Integer::valueOf);
  }

  @Override
  public List<Long> getLongList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.LONG_LIST, Long::valueOf);
  }

  @Override
  public List<Double> getDoubleList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.DOUBLE_LIST, Double::valueOf);
  }

  @Override
  public List<DateTime> getDateTimeList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.DATETIME_LIST, DateTime::parse);
  }

  @Override
  public List<String> getStringList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.STRING_LIST, Function.identity());
  }

  private void checkKey(String key) {
    Preconditions.checkNotNull(key);
    Preconditions.checkArgument(types.containsKey(key), "Key " + key + " does not exist");
  }

  private <T> T checkTypeAndGetNullable(String key, ValueType type, Function<String, T> function) {
    Preconditions.checkArgument(types.get(key).equals(type), "%s is expected to be a %s, but it actually is a %s", key, type.name(), types.get(key).name());
    Object value = values.get(key);
    if (value == null) {
      return null;
    } else {
      // when values are read from db, they are String
      // when values are created by user, they are T
      if (value instanceof String) {
        return function.apply((String)value);
      } else {
        return (T)value;
      }
    }
  }

  private JsonObject checkAndGetJson(String key) {
    Preconditions.checkArgument(types.get(key).category.equals(ValueType.Category.JSON), "%s is expected to be a json, it it actually is a %s", key, types.get(key));
    return (JsonObject)values.get(key);
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> checkTypeAndGetList(String key, ValueType type, Function<String, T> function) {
    Preconditions.checkArgument(types.get(key).equals(type));
    List valueList = (List)values.get(key);
    if (valueList == null || valueList.isEmpty()) {
      return Collections.emptyList();
    } else {
      if (valueList.get(0) instanceof String) {
        return ((List<String>)valueList).stream().map(function).collect(Collectors.toList());
      } else {
        return (List<T>)valueList;
      }
    }
  }

  @Override
  public int hashCode() {
    int hashCode = Objects.hashCode(recordId);
    hashCode += 19 * types.keySet().hashCode();
    hashCode += 19 * types.values().hashCode();
    hashCode += 19 * values.values().hashCode();
    return hashCode;
  }

  @Override
  public String toString() {
    return JsRecord.class.getSimpleName() +
        "{" +
        "recordId: " + recordId +
        ", types: " + types +
        ", values: " + values +
        "}";
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof JsRecord)) {
      return false;
    }

    JsRecord that = (JsRecord)other;
    return Objects.equals(this.recordId, that.recordId) &&
        Objects.equals(this.types, that.types) &&
        Objects.equals(this.values, that.values);
  }

}
