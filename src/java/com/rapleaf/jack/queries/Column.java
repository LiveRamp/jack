package com.rapleaf.jack.queries;

import java.util.Collection;

import com.rapleaf.jack.queries.where_operators.Between;
import com.rapleaf.jack.queries.where_operators.EqualTo;
import com.rapleaf.jack.queries.where_operators.GreaterThan;
import com.rapleaf.jack.queries.where_operators.GreaterThanOrEqualTo;
import com.rapleaf.jack.queries.where_operators.In;
import com.rapleaf.jack.queries.where_operators.IsNotNull;
import com.rapleaf.jack.queries.where_operators.IsNull;
import com.rapleaf.jack.queries.where_operators.LessThan;
import com.rapleaf.jack.queries.where_operators.LessThanOrEqualTo;
import com.rapleaf.jack.queries.where_operators.Match;
import com.rapleaf.jack.queries.where_operators.NotBetween;
import com.rapleaf.jack.queries.where_operators.NotEqualTo;
import com.rapleaf.jack.queries.where_operators.NotIn;

public class Column<T> {
  private static String DEFAULT_ID_FIELD = "id";

  protected String table;
  protected final Enum field;
  protected final Class type;

  protected Column(String table, Enum field, Class type) {
    this.table = table;
    this.field = field;
    this.type = type;
  }

  protected <M> Column(Column<M> that) {
    this.table = that.table;
    this.field = that.field;
    this.type = that.type;
  }

  public static Column<Long> fromId(String table) {
    return new Column<Long>(table, null, Long.class);
  }

  public static <T> Column<T> fromField(String table, Enum field, Class<T> fieldType) {
    return new Column<T>(table, field, fieldType);
  }

  public static Column<Long> fromTimestamp(String table, Enum field) {
    return new Column<Long>(table, field, java.sql.Timestamp.class);
  }

  public static Column<Long> fromDate(String table, Enum field) {
    return new Column<Long>(table, field, java.sql.Date.class);
  }

  public <M> Column<M> as(Class<M> type) {
    return new Column<M>(this.table, this.field, type);
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

  public GenericConstraint<T> isNotNull() {
    return new GenericConstraint<T>(this, new IsNotNull<T>());
  }

  public GenericConstraint<T> isNull() {
    return new GenericConstraint<T>(this, new IsNull<T>());
  }

  public GenericConstraint<T> equalTo(T value) {
    if (value != null) {
      return new GenericConstraint<T>(this, new EqualTo<T>(value));
    } else {
      return new GenericConstraint<T>(this, new IsNull<T>());
    }
  }

  public GenericConstraint<T> equalTo(Column<T> column) {
    if (column != null) {
      return new GenericConstraint<T>(this, new EqualTo<T>(column));
    } else {
      return new GenericConstraint<T>(this, new IsNull<T>());
    }
  }

  public GenericConstraint<T> notEqualTo(T value) {
    if (value != null) {
      return new GenericConstraint<T>(this, new NotEqualTo<T>(value));
    } else {
      return new GenericConstraint<T>(this, new IsNotNull<T>());
    }
  }

  public GenericConstraint<T> notEqualTo(Column<T> column) {
    if (column != null) {
      return new GenericConstraint<T>(this, new NotEqualTo<T>(column));
    } else {
      return new GenericConstraint<T>(this, new IsNotNull<T>());
    }
  }

  public GenericConstraint<T> greaterThan(T value) {
    return new GenericConstraint<T>(this, new GreaterThan<T>(value));
  }

  public GenericConstraint<T> greaterThan(Column<T> column) {
    return new GenericConstraint<T>(this, new GreaterThan<T>(column));
  }

  public GenericConstraint<T> greaterThanOrEqualTo(T value) {
    return new GenericConstraint<T>(this, new GreaterThanOrEqualTo<T>(value));
  }

  public GenericConstraint<T> greaterThanOrEqualTo(Column<T> value) {
    return new GenericConstraint<T>(this, new GreaterThanOrEqualTo<T>(value));
  }

  public GenericConstraint<T> lessThan(T value) {
    return new GenericConstraint<T>(this, new LessThan<T>(value));
  }

  public GenericConstraint<T> lessThan(Column<T> column) {
    return new GenericConstraint<T>(this, new LessThan<T>(column));
  }

  public GenericConstraint<T> lessThanOrEqualTo(T value) {
    return new GenericConstraint<T>(this, new LessThanOrEqualTo<T>(value));
  }

  public GenericConstraint<T> lessThanOrEqualTo(Column<T> value) {
    return new GenericConstraint<T>(this, new LessThanOrEqualTo<T>(value));
  }

  public GenericConstraint<T> between(T min, T max) {
    return new GenericConstraint<T>(this, new Between<T>(min, max));
  }

  public GenericConstraint<T> between(Column<T> min, T max) {
    return new GenericConstraint<T>(this, new Between<T>(min, max));
  }

  public GenericConstraint<T> between(T min, Column<T> max) {
    return new GenericConstraint<T>(this, new Between<T>(min, max));
  }

  public GenericConstraint<T> between(Column<T> min, Column<T> max) {
    return new GenericConstraint<T>(this, new Between<T>(min, max));
  }

  public GenericConstraint<T> notBetween(T min, T max) {
    return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
  }

  public GenericConstraint<T> notBetween(Column<T> min, T max) {
    return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
  }

  public GenericConstraint<T> notBetween(T min, Column<T> max) {
    return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
  }

  public GenericConstraint<T> notBetween(Column<T> min, Column<T> max) {
    return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
  }

  public GenericConstraint<T> in(T value, T... otherValues) {
    return new GenericConstraint<T>(this, new In<T>(value, otherValues));
  }

  public GenericConstraint<T> in(Collection<T> values) {
    return new GenericConstraint<T>(this, new In<T>(values));
  }

  public GenericConstraint<T> notIn(T value, T... otherValues) {
    return new GenericConstraint<T>(this, new NotIn<T>(value, otherValues));
  }

  public GenericConstraint<T> notIn(Collection<T> values) {
    return new GenericConstraint<T>(this, new NotIn<T>(values));
  }

  public GenericConstraint<String> matches(String pattern) {
    return new GenericConstraint<String>(this.as(String.class), new Match(pattern));
  }

  public GenericConstraint<String> contains(String string) {
    return new GenericConstraint<String>(this.as(String.class), new Match("%" + string + "%"));
  }

  public GenericConstraint<String> startsWith(String start) {
    return new GenericConstraint<String>(this.as(String.class), new Match(start + "%"));
  }

  public GenericConstraint<String> endsWith(String end) {
    return new GenericConstraint<String>(this.as(String.class), new Match("%" + end));
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
