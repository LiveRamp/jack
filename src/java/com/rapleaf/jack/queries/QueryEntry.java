package com.rapleaf.jack.queries;

import java.sql.Timestamp;
import java.util.Map;

import com.google.common.collect.Maps;

import com.rapleaf.jack.Column;
import com.rapleaf.jack.ModelWithId;

public class QueryEntry {
  private final Map<Column, Object> entry;

  QueryEntry(int fieldCount) {
    this.entry = Maps.newHashMapWithExpectedSize(fieldCount);
  }

  void addModelField(Column column, Object value) {
    entry.put(column, value);
  }

  public boolean contains(Column column) {
    return entry.containsKey(column);
  }

  public int fieldCount() {
    return entry.size();
  }

  public Integer getInt(Column column) {
    Object value = checkTypeAndReturnObject(column, Integer.class);
    return value == null ? null : ((Number)value).intValue();
  }

  public Integer getIntFromLong(Column column) {
    Object value = checkTypeAndReturnObject(column, Long.class);
    return value == null ? null : ModelWithId.safeLongToInt(getLong(column));
  }

  public Long getLong(Column column) {
    Object value = checkTypeAndReturnObject(column, Long.class);
    if (value == null) {
      return null;
    } else if (value instanceof Timestamp) {
      return ((Timestamp)value).getTime();
    } else {
      return ((Number)value).longValue();
    }
  }

  public String getString(Column column) {
    Object value = checkTypeAndReturnObject(column, String.class);
    return value == null ? null : (String)value;
  }

  public byte[] getByteArray(Column column) {
    Object value = checkTypeAndReturnObject(column, byte[].class);
    return value == null ? null : (byte[])value;
  }

  public Double getDouble(Column column) {
    Object value = checkTypeAndReturnObject(column, Double.class);
    return value == null ? null : ((Number)value).doubleValue();
  }

  public Boolean getBoolean(Column column) {
    Object value = checkTypeAndReturnObject(column, Boolean.class);
    return value == null ? null : (Boolean)value;
  }

  private Object checkTypeAndReturnObject(Column column, Class clazz) {
    if (column.getType().equals(clazz)) {
      return getObject(column);
    } else {
      throw new RuntimeException(getExceptionMessage(column, clazz));
    }
  }

  private Object getObject(Column column) {
    if (entry.containsKey(column)) {
      return entry.get(column);
    } else {
      throw new RuntimeException("Field " + column.toString() + " is not included in the query");
    }
  }

  private String getExceptionMessage(Column column, Class clazz) throws RuntimeException {
    return "Field " + column.toString() + " is not of type " + clazz.getSimpleName();
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
