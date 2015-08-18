package com.rapleaf.jack.queries;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.google.common.collect.Maps;

import com.rapleaf.jack.AttributesWithId;
import com.rapleaf.jack.util.JackUtility;

public class Record {
  private final Collection<Table> tables;
  private final Map<Column, Object> columns;

  Record(Collection<Table> tables, int columnCount) {
    this.tables = tables;
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

  public Map<Column, Object> getColumns() {
    return columns;
  }

  public Integer getInt(Column column) {
    Object value = checkTypeAndReturnObject(column, Integer.class);
    return value == null ? null : ((Number)value).intValue();
  }

  public Integer getIntFromLong(Column column) {
    Object value = checkTypeAndReturnObject(column, Long.class);
    return value == null ? null : JackUtility.safeLongToInt(getLong(column));
  }

  public Long getLong(Column column) {
    Object value = checkTypeAndReturnObject(column, Long.class);
    if (value == null) {
      return null;
    } else if (value instanceof Date) {
      return ((Date)value).getTime();
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

  @SuppressWarnings("unchecked")
  public <A extends AttributesWithId> A getAttribute(Table tableType) {
    Constructor<A> constructor = null;
    String tableName = null;
    for (Table table : tables) {
      if (table.getAttributeType().equals(tableType.getAttributeType())) {
        tableName = table.getAlias();
        try {
          constructor = ((Class<A>)table.getAttributeType()).getConstructor(Long.class);
        } catch (NoSuchMethodException e) {
          throw new RuntimeException(e);
        }
        break;
      }
    }

    if (constructor == null || tableName == null) {
      throw new RuntimeException("No columns from Table " + tableName + " are included in this record");
    }

    Long id = null;
    Map<Enum, Object> fieldMap = Maps.newHashMap();
    for (Map.Entry<Column, Object> entry : columns.entrySet()) {
      Column column = entry.getKey();
      if (column.getTable().equals(tableName)) {
        if (column.getField() != null) {
          fieldMap.put(column.getField(), entry.getValue());
        } else if (id == null) {
          id = ((Number)(entry.getValue())).longValue();
        } else {
          throw new RuntimeException("The record contains multiple IDs for Table " + tableName);
        }
      }
    }

    A attribute;
    try {
      attribute = constructor.newInstance(id);
      for (Map.Entry<Enum, Object> entry : fieldMap.entrySet()) {
        attribute.setField(entry.getKey().name(), entry.getValue());
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return attribute;
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
