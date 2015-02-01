package com.rapleaf.jack.queries;

import java.sql.Timestamp;
import java.util.Map;

import com.google.common.collect.Maps;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.ModelWithId;

public class QueryEntry {
  private final Map<ModelField, Object> entry;

  QueryEntry(int fieldCount) {
    this.entry = Maps.newHashMapWithExpectedSize(fieldCount);
  }

  void addModelField(ModelField modelField, Object value) {
    entry.put(modelField, value);
  }

  public boolean contains(ModelField modelField) {
    return entry.containsKey(modelField);
  }

  public Integer getInt(ModelField modelField) {
    return ((Number)checkTypeAndReturnObject(modelField, Integer.class)).intValue();
  }

  public Integer getIntFromLong(ModelField modelField) {
    return ModelWithId.safeLongToInt(getLong(modelField));
  }

  public Long getLong(ModelField modelField) {
    Object value = checkTypeAndReturnObject(modelField, Long.class);
    if (value instanceof Timestamp) {
      return ((Timestamp)value).getTime();
    } else {
      return ((Number)value).longValue();
    }
  }

  public String getString(ModelField modelField) {
    return (String)checkTypeAndReturnObject(modelField, String.class);
  }

  public byte[] getByteArray(ModelField modelField) {
    return (byte[])checkTypeAndReturnObject(modelField, byte[].class);
  }

  public Double getDouble(ModelField modelField) {
    return ((Number)checkTypeAndReturnObject(modelField, Double.class)).doubleValue();
  }

  public Boolean getBoolean(ModelField modelField) {
    return (Boolean)checkTypeAndReturnObject(modelField, Boolean.class);
  }

  private Object checkTypeAndReturnObject(ModelField modelField, Class clazz) {
    if (modelField.getType().equals(clazz)) {
      return getObject(modelField);
    } else {
      throw new RuntimeException(getExceptionMessage(modelField, clazz));
    }
  }

  private Object getObject(ModelField modelField) {
    if (entry.containsKey(modelField)) {
      return entry.get(modelField);
    } else {
      throw new RuntimeException("Field " + modelField.toString() + " is not included in the query");
    }
  }

  private String getExceptionMessage(ModelField modelField, Class clazz) throws RuntimeException {
    return "Field " + modelField.toString() + " is not of type " + clazz.getSimpleName();
  }

  @Override
  public String toString() {
    return entry.toString();
  }

  @Override
  public int hashCode() {
    return entry.hashCode();
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof QueryEntry && this.entry.equals(((QueryEntry)that).entry);
  }

}
