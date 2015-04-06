package com.rapleaf.jack.queries;

import java.util.Collection;

import com.rapleaf.jack.queries.where_operators.*;

public class Column {
  private static String DEFAULT_ID_FIELD = "id";

  protected String table;
  protected final Enum field;
  protected final Class type;

  protected Column(String table, Enum field, Class type) {
    this.table = table;
    this.field = field;
    this.type = type;
  }

  protected Column(Column that) {
    this.table = that.table;
    this.field = that.field;
    this.type = that.type;
  }

  public static Column fromId(String table) {
    return new Column(table, null, Long.class);
  }

  public static Column fromField(String table, Enum field, Class fieldType) {
    return new Column(table, field, fieldType);
  }

  public String getTable() {
    return table;
  }

  public Enum getField() {
    return field;
  }

  public Class getType() {
    return type;
  }

  public String getSqlKeyword() {
    StringBuilder sqlKeyword = new StringBuilder();

    if (table != null) {
      sqlKeyword.append(table).append(".");
    }

    if (field != null) {
      sqlKeyword.append(field.toString());
    } else {
      sqlKeyword.append(DEFAULT_ID_FIELD);
    }

    return sqlKeyword.toString();
  }

  public <T> GenericConstraint<T> isNotNull() {
    return new GenericConstraint<T>(this, new IsNotNull<T>());
  }

  public <T> GenericConstraint<T> isNull() {
    return new GenericConstraint<T>(this, new IsNull<T>());
  }

  public <T> GenericConstraint<T> equalTo(T value) {
    if (value != null) {
      return new GenericConstraint<T>(this, new EqualTo<T>(value));
    } else {
      return new GenericConstraint<T>(this, new IsNull<T>());
    }
  }

  public <T> GenericConstraint<T> equalTo(Column column) {
    if (column != null) {
      return new GenericConstraint<T>(this, new EqualTo<T>(column));
    } else {
      return new GenericConstraint<T>(this, new IsNull<T>());
    }
  }

  public <T> GenericConstraint<T> notEqualTo(T value) {
    if (value != null) {
      return new GenericConstraint<T>(this, new NotEqualTo<T>(value));
    } else {
      return new GenericConstraint<T>(this, new IsNotNull<T>());
    }
  }

  public <T> GenericConstraint<T> notEqualTo(Column column) {
    if (column != null) {
      return new GenericConstraint<T>(this, new NotEqualTo<T>(column));
    } else {
      return new GenericConstraint<T>(this, new IsNotNull<T>());
    }
  }

  public <T> GenericConstraint<T> greaterThan(T value) {
    return new GenericConstraint<T>(this, new GreaterThan<T>(value));
  }

  public <T> GenericConstraint<T> greaterThan(Column column) {
    return new GenericConstraint<T>(this, new GreaterThan<T>(column));
  }

  public <T> GenericConstraint<T> greaterThanOrEqualTo(T value) {
    return new GenericConstraint<T>(this, new GreaterThanOrEqualTo<T>(value));
  }

  public <T> GenericConstraint<T> greaterThanOrEqualTo(Column value) {
    return new GenericConstraint<T>(this, new GreaterThanOrEqualTo<T>(value));
  }

  public <T> GenericConstraint<T> lessThan(T value) {
    return new GenericConstraint<T>(this, new LessThan<T>(value));
  }

  public <T> GenericConstraint<T> lessThan(Column column) {
    return new GenericConstraint<T>(this, new LessThan<T>(column));
  }

  public <T> GenericConstraint<T> lessThanOrEqualTo(T value) {
    return new GenericConstraint<T>(this, new LessThanOrEqualTo<T>(value));
  }

  public <T> GenericConstraint<T> lessThanOrEqualTo(Column value) {
    return new GenericConstraint<T>(this, new LessThanOrEqualTo<T>(value));
  }

  public <T> GenericConstraint<T> between(Comparable min, Comparable max) {
    return new GenericConstraint<T>(this, new Between<T>((T)min, (T)max));
  }

  public <T> GenericConstraint<T> between(Column min, Comparable max) {
    return new GenericConstraint<T>(this, new Between<T>(min, (T)max));
  }

  public <T> GenericConstraint<T> between(Comparable min, Column max) {
    return new GenericConstraint<T>(this, new Between<T>((T)min, max));
  }

  public <T> GenericConstraint<T> between(Column min, Column max) {
    return new GenericConstraint<T>(this, new Between<T>(min, max));
  }

  public <T> GenericConstraint<T> notBetween(T min, T max) {
    return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
  }

  public <T> GenericConstraint<T> notBetween(Column min, T max) {
    return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
  }

  public <T> GenericConstraint<T> notBetween(T min, Column max) {
    return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
  }

  public <T> GenericConstraint<T> notBetween(Column min, Column max) {
    return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
  }

  public <T> GenericConstraint<T> in(T value, T... otherValues) {
    return new GenericConstraint<T>(this, new In<T>(value, otherValues));
  }

  public <T> GenericConstraint<T> in(Collection<T> values) {
    return new GenericConstraint<T>(this, new In<T>(values));
  }

  public <T> GenericConstraint<T> notIn(T value, T... otherValues) {
    return new GenericConstraint<T>(this, new NotIn<T>(value, otherValues));
  }

  public <T> GenericConstraint<T> notIn(Collection<T> values) {
    return new GenericConstraint<T>(this, new NotIn<T>(values));
  }

  public GenericConstraint<String> matches(String pattern) {
    return new GenericConstraint<String>(this, new Match(pattern));
  }

  public GenericConstraint<String> contains(String string) {
    return new GenericConstraint<String>(this, new Match("%" + string + "%"));
  }

  public GenericConstraint<String> startsWith(String start) {
    return new GenericConstraint<String>(this, new Match(start + "%"));
  }

  public GenericConstraint<String> endsWith(String end) {
    return new GenericConstraint<String>(this, new Match("%" + end));
  }

  @Override
  public String toString() {
    return getSqlKeyword();
  }

  @Override
  public int hashCode() {
    return getSqlKeyword().hashCode();
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof Column && this.toString().equals(((Column)that).toString());
  }
}
