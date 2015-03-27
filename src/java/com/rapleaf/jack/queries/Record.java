package com.rapleaf.jack.queries;

import java.sql.Timestamp;
import java.util.Map;

import com.google.common.collect.Maps;

import com.rapleaf.jack.ModelWithId;

public class Record {
  private final Map<Column, Object> columns;

  Record(int columnCount) {
    this.columns = Maps.newHashMapWithExpectedSize(columnCount);
  }

  void addColumn(Column column, Object value) {
    columns.put(column, value);
  }

  public boolean contains(Column column) {
    return columns.containsKey(column);
  }

  public int columnCount() {
    return columns.size();
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
    if (columns.containsKey(column)) {
      return columns.get(column);
    } else {
      throw new RuntimeException("Column " + column.toString() + " is not included in the query");
    }
  }

  private String getExceptionMessage(Column column, Class clazz) throws RuntimeException {
    return "Column " + column.toString() + " is not of type " + clazz.getSimpleName();
  }

  @Override
  public String toString() {
    return columns.toString();
  }

  @Override
  public int hashCode() {
    return columns.hashCode();
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof Record && this.columns.equals(((Record)that).columns);
  }
}
