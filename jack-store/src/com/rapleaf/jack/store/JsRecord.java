package com.rapleaf.jack.store;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;

import com.rapleaf.jack.exception.JackRuntimeException;

public class JsRecord {

  private final Long scopeId;
  private final Map<String, ValueType> types;
  private final Map<String, Object> values;

  public JsRecord(Long scopeId, Map<String, ValueType> types, Map<String, Object> values) {
    Preconditions.checkNotNull(types);
    Preconditions.checkNotNull(values);
    Preconditions.checkArgument(types.keySet().equals(values.keySet()));
    this.scopeId = scopeId;
    this.types = Maps.newHashMap(types);
    this.values = Maps.newHashMap(values);
  }

  public static JsRecord empty(Long scopeId) {
    return new JsRecord(scopeId, Collections.emptyMap(), Collections.emptyMap());
  }

  public Long getScopeId() {
    return scopeId;
  }

  public Set<String> keySet() {
    return types.keySet();
  }

  public boolean isEmpty() {
    return types.isEmpty();
  }

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

  public Boolean getBoolean(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.BOOLEAN, Boolean::valueOf);
  }

  public Integer getInt(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.INT, Integer::valueOf);
  }

  public Long getLong(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.LONG, Long::valueOf);
  }

  public Double getDouble(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.DOUBLE, Double::valueOf);
  }

  public DateTime getDateTime(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.DATETIME, DateTime::parse);
  }

  public String getString(String key) {
    checkKey(key);
    return checkTypeAndGetNullable(key, ValueType.STRING, Function.identity());
  }

  public JsonObject getJson(String key) {
    checkKey(key);
    return checkAndGetJson(key);
  }

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

  public List<Boolean> getBooleanList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.BOOLEAN_LIST, Boolean::valueOf);
  }

  public List<Integer> getIntList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.INT_LIST, Integer::valueOf);
  }

  public List<Long> getLongList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.LONG_LIST, Long::valueOf);
  }

  public List<Double> getDoubleList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.DOUBLE_LIST, Double::valueOf);
  }

  public List<DateTime> getDateTimeList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.DATETIME_LIST, DateTime::parse);
  }

  public List<String> getStringList(String key) {
    checkKey(key);
    return checkTypeAndGetList(key, ValueType.STRING_LIST, Function.identity());
  }

  private void checkKey(String key) {
    Preconditions.checkNotNull(key);
    Preconditions.checkArgument(types.containsKey(key), "Key" + key + " does not exist");
  }

  private <T> T checkTypeAndGetNullable(String key, ValueType type, Function<String, T> function) {
    Preconditions.checkArgument(types.get(key).equals(type));
    String value = (String)values.get(key);
    if (value == null) {
      return null;
    } else {
      return function.apply(value);
    }
  }

  private JsonObject checkAndGetJson(String key) {
    Preconditions.checkArgument(types.get(key).category.equals(ValueType.Category.JSON));
    return (JsonObject)values.get(key);
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> checkTypeAndGetList(String key, ValueType type, Function<String, T> function) {
    Preconditions.checkArgument(types.get(key).equals(type));
    List<String> valueList = (List<String>)values.get(key);
    if (valueList == null || valueList.isEmpty()) {
      return Collections.emptyList();
    } else {
      return valueList.stream().map(function).collect(Collectors.toList());
    }
  }

  @Override
  public int hashCode() {
    return 19 * (types.keySet().hashCode() + 19 * types.values().hashCode()) + values.values().hashCode();
  }

  @Override
  public String toString() {
    return JsRecord.class.getSimpleName() +
        "{" +
        "types=" + types +
        ",values=" + values +
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
    return Objects.equals(this.types, that.types) && Objects.equals(this.values, that.values);
  }

}
