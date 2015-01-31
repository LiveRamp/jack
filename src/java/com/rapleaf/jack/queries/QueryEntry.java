package com.rapleaf.jack.queries;

import java.util.Map;

import com.google.common.collect.Maps;

import com.rapleaf.jack.ModelField;

public class QueryEntry {
  private final Map<ModelField, Object> entry;

  QueryEntry(int fieldCount) {
    this.entry = Maps.newHashMapWithExpectedSize(fieldCount);
  }

  void addModelField(ModelField modelField, Object value) {
    entry.put(modelField, value);
  }

  public Object getObject(ModelField modelField) {
    if (entry.containsKey(modelField)) {
      return entry.get(modelField);
    } else {
      throw new RuntimeException("Field " + modelField.toString() + " is not included in the query");
    }
  }

  private Object checkTypeAndReturnObject(ModelField modelField, Class clazz) {
    if (modelField.getType().equals(clazz)) {
      return getObject(modelField);
    } else {
      throw new RuntimeException(getExceptionMessage(modelField, clazz));
    }
  }

  public Integer getInteger(ModelField modelField) {
    return (Integer)checkTypeAndReturnObject(modelField, Integer.class);
  }

  public Long getLong(ModelField modelField) {
    return (Long)checkTypeAndReturnObject(modelField, Long.class);
  }

  public String getString(ModelField modelField) {
    return (String)checkTypeAndReturnObject(modelField, String.class);
  }

  public byte[] getByteArray(ModelField modelField) {
    return (byte[])checkTypeAndReturnObject(modelField, byte[].class);
  }

  public Double getDouble(ModelField modelField) {
    return (Double)checkTypeAndReturnObject(modelField, Double.class);
  }

  public Boolean getBoolean(ModelField modelField) {
    return (Boolean)checkTypeAndReturnObject(modelField, Boolean.class);
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
