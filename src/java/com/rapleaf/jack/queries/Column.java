package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import com.rapleaf.jack.queries.where_operators.Between;
import com.rapleaf.jack.queries.where_operators.EqualTo;
import com.rapleaf.jack.queries.where_operators.GenericOperator;
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
import com.rapleaf.jack.queries.where_operators.WhereOperator;
import com.rapleaf.jack.util.JackUtility;

public class Column<T> {
  private static final String DEFAULT_ID_FIELD = "id";

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

  public GenericConstraint isNotNull() {
    return new GenericConstraint<T>(this, new IsNotNull<T>());
  }

  public GenericConstraint isNull() {
    return new GenericConstraint<T>(this, new IsNull<T>());
  }

  public GenericConstraint equalTo(T value) {
    if (value != null) {
      if (isDateColumn()) {
        return createDateConstraint(new EqualTo<Long>(Long.class.cast(value)));
      } else {
        return new GenericConstraint<T>(this, new EqualTo<T>(value));
      }
    } else {
      return new GenericConstraint<T>(this, new IsNull<T>());
    }
  }

  public GenericConstraint equalTo(Column<T> column) {
    if (column != null) {
      return new GenericConstraint<T>(this, new EqualTo<T>(column));
    } else {
      return new GenericConstraint<T>(this, new IsNull<T>());
    }
  }

  public GenericConstraint notEqualTo(T value) {
    if (value != null) {
      if (isDateColumn()) {
        return createDateConstraint(new NotEqualTo<Long>(Long.class.cast(value)));
      } else {
        return new GenericConstraint<T>(this, new NotEqualTo<T>(value));
      }
    } else {
      return new GenericConstraint<T>(this, new IsNotNull<T>());
    }
  }

  public GenericConstraint notEqualTo(Column<T> column) {
    if (column != null) {
      return new GenericConstraint<T>(this, new NotEqualTo<T>(column));
    } else {
      return new GenericConstraint<T>(this, new IsNotNull<T>());
    }
  }

  public GenericConstraint greaterThan(T value) {
    if (isDateColumn()) {
      return createDateConstraint(new GreaterThan<Long>(Long.class.cast(value)));
    } else {
      return new GenericConstraint<T>(this, new GreaterThan<T>(value));
    }
  }

  public GenericConstraint greaterThan(Column<T> column) {
    return new GenericConstraint<T>(this, new GreaterThan<T>(column));
  }

  public GenericConstraint greaterThanOrEqualTo(T value) {
    if (isDateColumn()) {
      return createDateConstraint(new GreaterThanOrEqualTo<Long>(Long.class.cast(value)));
    } else {
      return new GenericConstraint<T>(this, new GreaterThanOrEqualTo<T>(value));
    }
  }

  public GenericConstraint greaterThanOrEqualTo(Column<T> value) {
    return new GenericConstraint<T>(this, new GreaterThanOrEqualTo<T>(value));
  }

  public GenericConstraint lessThan(T value) {
    if (isDateColumn()) {
      return createDateConstraint(new LessThan<Long>(Long.class.cast(value)));
    } else {
      return new GenericConstraint<T>(this, new LessThan<T>(value));
    }
  }

  public GenericConstraint lessThan(Column<T> column) {
    return new GenericConstraint<T>(this, new LessThan<T>(column));
  }

  public GenericConstraint lessThanOrEqualTo(T value) {
    if (isDateColumn()) {
      return createDateConstraint(new LessThanOrEqualTo<Long>(Long.class.cast(value)));
    } else {
      return new GenericConstraint<T>(this, new LessThanOrEqualTo<T>(value));
    }
  }

  public GenericConstraint lessThanOrEqualTo(Column<T> value) {
    return new GenericConstraint<T>(this, new LessThanOrEqualTo<T>(value));
  }

  public GenericConstraint between(T min, T max) {
    if (isDateColumn()) {
      return createDateConstraint(new Between<Long>(Long.class.cast(min), Long.class.cast(max)));
    } else {
      return new GenericConstraint<T>(this, new Between<T>(min, max));
    }
  }

  public GenericConstraint between(Column<T> min, T max) {
    if (isDateColumn()) {
      return createDateConstraint(new Between<Long>(min.as(Long.class), Long.class.cast(max)));
    } else {
      return new GenericConstraint<T>(this, new Between<T>(min, max));
    }
  }

  public GenericConstraint between(T min, Column<T> max) {
    if (isDateColumn()) {
      return createDateConstraint(new Between<Long>(Long.class.cast(min), max.as(Long.class)));
    } else {
      return new GenericConstraint<T>(this, new Between<T>(min, max));
    }
  }

  public GenericConstraint between(Column<T> min, Column<T> max) {
    return new GenericConstraint<T>(this, new Between<T>(min, max));
  }

  public GenericConstraint notBetween(T min, T max) {
    if (isDateColumn()) {
      return createDateConstraint(new NotBetween<Long>(Long.class.cast(min), Long.class.cast(max)));
    } else {
      return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
    }
  }

  public GenericConstraint notBetween(Column<T> min, T max) {
    if (isDateColumn()) {
      return createDateConstraint(new NotBetween<Long>(min.as(Long.class), Long.class.cast(max)));
    } else {
      return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
    }
  }

  public GenericConstraint notBetween(T min, Column<T> max) {
    if (isDateColumn()) {
      return createDateConstraint(new NotBetween<Long>(Long.class.cast(min), max.as(Long.class)));
    } else {
      return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
    }
  }

  public GenericConstraint notBetween(Column<T> min, Column<T> max) {
    return new GenericConstraint<T>(this, new NotBetween<T>(min, max));
  }

  public GenericConstraint in(T value, T... otherValues) {
    if (isDateColumn()) {
      return createDateConstraint(new In<Long>(Long.class.cast(value), Arrays.copyOf(otherValues, otherValues.length, Long[].class)));
    } else {
      return new GenericConstraint<T>(this, new In<T>(value, otherValues));
    }
  }

  public GenericConstraint in(Collection<T> values) {
    if (isDateColumn()) {
      return createDateConstraint(new In<Long>(Collections2.transform(values, JackUtility.LONG_CASTER)));
    } else {
      return new GenericConstraint<T>(this, new In<T>(values));
    }
  }

  public GenericConstraint notIn(T value, T... otherValues) {
    if (isDateColumn()) {
      return createDateConstraint(new NotIn<Long>(Long.class.cast(value), Arrays.copyOf(otherValues, otherValues.length, Long[].class)));
    } else {
      return new GenericConstraint<T>(this, new NotIn<T>(value, otherValues));
    }
  }

  public GenericConstraint notIn(Collection<T> values) {
    if (isDateColumn()) {
      return createDateConstraint(new NotIn<Long>(Collections2.transform(values, JackUtility.LONG_CASTER)));
    } else {
      return new GenericConstraint<T>(this, new NotIn<T>(values));
    }
  }

  public GenericConstraint matches(String pattern) {
    return new GenericConstraint<String>(this.as(String.class), new Match(pattern));
  }

  public GenericConstraint matches(Column<String> col, String prefix, String suffix) {
    return new GenericConstraint<String>(this.as(String.class), new Match(col, prefix, suffix));
  }

  public GenericConstraint contains(String string) {
    return matches("%" + string + "%");
  }

  public GenericConstraint contains(Column<String> col) {
    return matches(col, "%", "%");
  }

  public GenericConstraint startsWith(String start) {
    return new GenericConstraint<String>(this.as(String.class), new Match(start + "%"));
  }

  public GenericConstraint endsWith(String end) {
    return new GenericConstraint<String>(this.as(String.class), new Match("%" + end));
  }

  boolean isDateColumn() {
    return java.util.Date.class.isAssignableFrom(type);
  }

  private GenericConstraint createDateConstraint(WhereOperator<Long> operator) {
    return new GenericConstraint<String>(
        this.as(String.class),
        new GenericOperator<String>(
            operator.getSqlStatement(),
            Lists.transform(operator.getParameters(), JackUtility.FORMATTER_FUNCTION_MAP.get(type))
        )
    );
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
