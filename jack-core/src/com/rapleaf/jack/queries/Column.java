package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

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

  /**
   * Construct ID column. This constructor is mainly for internal use.
   */
  public static Column<Long> fromId(String table) {
    return new Column<>(table, null, Long.class);
  }

  /**
   * Construct column other than ID, timestamp or date. This constructor is mainly for internal use.
   */
  public static <T> Column<T> fromField(String table, Enum field, Class<T> fieldType) {
    return new Column<>(table, field, fieldType);
  }

  /**
   * Construct timestamp column. This constructor is mainly for internal use.
   */
  public static Column<Long> fromTimestamp(String table, Enum field) {
    return new Column<>(table, field, java.sql.Timestamp.class);
  }

  /**
   * Construct date column. This constructor is mainly for internal use.
   */
  public static Column<Long> fromDate(String table, Enum field) {
    return new Column<>(table, field, java.sql.Date.class);
  }

  /**
   * Convert column type.
   * <p>
   * This method is typically used in WHERE clause. For example:
   * User.ID.equalTo(Post.USER_ID.as(Long.class))
   */
  public <M> Column<M> as(Class<M> type) {
    return new Column<>(this.table, this.field, type);
  }

  /**
   * Change table alias.
   */
  Column<T> forTable(String tableAlias) {
    return new Column<>(tableAlias, this.field, type);
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

  /**
   * @return SQL keyword for select clause.
   */
  String getSelectKeyword() {
    return getSqlKeyword();
  }

  /**
   * @return column alias for select clause.
   */
  String getSelectAlias() {
    return getSqlKeyword();
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
    return new GenericConstraint<>(this, new IsNotNull<>());
  }

  public GenericConstraint isNull() {
    return new GenericConstraint<>(this, new IsNull<>());
  }

  public GenericConstraint equalTo(T value) {
    if (value != null) {
      if (isDateColumn()) {
        return createDateConstraint(new EqualTo<>((Long)value));
      } else {
        return new GenericConstraint<>(this, new EqualTo<>(value));
      }
    } else {
      return new GenericConstraint<>(this, new IsNull<>());
    }
  }

  public GenericConstraint equalTo(Column<T> column) {
    if (column != null) {
      return new GenericConstraint<>(this, new EqualTo<>(column));
    } else {
      return new GenericConstraint<>(this, new IsNull<>());
    }
  }

  public GenericConstraint equalTo(SingleValue<T> singleValue) {
    return new GenericConstraint<T>(this, new EqualTo<T>(singleValue));
  }

  public GenericConstraint notEqualTo(T value) {
    if (value != null) {
      if (isDateColumn()) {
        return createDateConstraint(new NotEqualTo<>((Long)value));
      } else {
        return new GenericConstraint<>(this, new NotEqualTo<>(value));
      }
    } else {
      return new GenericConstraint<>(this, new IsNotNull<>());
    }
  }

  public GenericConstraint notEqualTo(Column<T> column) {
    if (column != null) {
      return new GenericConstraint<>(this, new NotEqualTo<>(column));
    } else {
      return new GenericConstraint<>(this, new IsNotNull<>());
    }
  }

  public GenericConstraint notEqualTo(SingleValue<T> singleValue) {
    return new GenericConstraint<T>(this, new NotEqualTo<T>(singleValue));
  }

  public GenericConstraint greaterThan(T value) {
    if (isDateColumn()) {
      return createDateConstraint(new GreaterThan<>((Long)value));
    } else {
      return new GenericConstraint<>(this, new GreaterThan<>(value));
    }
  }

  public GenericConstraint greaterThan(Column<T> column) {
    return new GenericConstraint<>(this, new GreaterThan<>(column));
  }

  public GenericConstraint greaterThan(SingleValue<T> singleValue) {
    return new GenericConstraint<T>(this, new GreaterThan<T>(singleValue));
  }

  public GenericConstraint greaterThanOrEqualTo(T value) {
    if (isDateColumn()) {
      return createDateConstraint(new GreaterThanOrEqualTo<>(Long.class.cast(value)));
    } else {
      return new GenericConstraint<>(this, new GreaterThanOrEqualTo<>(value));
    }
  }

  public GenericConstraint greaterThanOrEqualTo(Column<T> value) {
    return new GenericConstraint<>(this, new GreaterThanOrEqualTo<>(value));
  }

  public GenericConstraint greaterThanOrEqualTo(SingleValue<T> singleValue) {
    return new GenericConstraint<T>(this, new GreaterThanOrEqualTo<T>(singleValue));
  }

  public GenericConstraint lessThan(T value) {
    if (isDateColumn()) {
      return createDateConstraint(new LessThan<>(Long.class.cast(value)));
    } else {
      return new GenericConstraint<>(this, new LessThan<>(value));
    }
  }

  public GenericConstraint lessThan(Column<T> column) {
    return new GenericConstraint<>(this, new LessThan<>(column));
  }

  public GenericConstraint lessThan(SingleValue<T> singleValue) {
    return new GenericConstraint<T>(this, new LessThan<T>(singleValue));
  }

  public GenericConstraint lessThanOrEqualTo(T value) {
    if (isDateColumn()) {
      return createDateConstraint(new LessThanOrEqualTo<>(Long.class.cast(value)));
    } else {
      return new GenericConstraint<>(this, new LessThanOrEqualTo<>(value));
    }
  }

  public GenericConstraint lessThanOrEqualTo(Column<T> value) {
    return new GenericConstraint<>(this, new LessThanOrEqualTo<>(value));
  }

  public GenericConstraint lessThanOrEqualTo(SingleValue<T> singleValue) {
    return new GenericConstraint<T>(this, new LessThanOrEqualTo<T>(singleValue));
  }

  public GenericConstraint between(T min, T max) {
    if (isDateColumn()) {
      return createDateConstraint(new Between<>((Long)min, (Long)max));
    } else {
      return new GenericConstraint<>(this, new Between<>(min, max));
    }
  }

  public GenericConstraint between(Column<T> min, T max) {
    if (isDateColumn()) {
      return createDateConstraint(new Between<>(min.as(Long.class), (Long)max));
    } else {
      return new GenericConstraint<>(this, new Between<>(min, max));
    }
  }

  public GenericConstraint between(T min, Column<T> max) {
    if (isDateColumn()) {
      return createDateConstraint(new Between<>((Long)min, max.as(Long.class)));
    } else {
      return new GenericConstraint<>(this, new Between<>(min, max));
    }
  }

  public GenericConstraint between(Column<T> min, Column<T> max) {
    return new GenericConstraint<>(this, new Between<>(min, max));
  }

  public GenericConstraint notBetween(T min, T max) {
    if (isDateColumn()) {
      return createDateConstraint(new NotBetween<>((Long)min, (Long)max));
    } else {
      return new GenericConstraint<>(this, new NotBetween<>(min, max));
    }
  }

  public GenericConstraint notBetween(Column<T> min, T max) {
    if (isDateColumn()) {
      return createDateConstraint(new NotBetween<>(min.as(Long.class), (Long)max));
    } else {
      return new GenericConstraint<>(this, new NotBetween<>(min, max));
    }
  }

  public GenericConstraint notBetween(T min, Column<T> max) {
    if (isDateColumn()) {
      return createDateConstraint(new NotBetween<>((Long)min, max.as(Long.class)));
    } else {
      return new GenericConstraint<>(this, new NotBetween<>(min, max));
    }
  }

  public GenericConstraint notBetween(Column<T> min, Column<T> max) {
    return new GenericConstraint<>(this, new NotBetween<>(min, max));
  }

  public GenericConstraint<?> in(T value, T... otherValues) {
    if (isDateColumn()) {
      return createDateConstraint(new In<>((Long)value, Arrays.copyOf(otherValues, otherValues.length, Long[].class)));
    } else {
      return new GenericConstraint<>(this, new In<>(value, otherValues));
    }
  }

  public GenericConstraint in(Collection<T> values) {
    if (isDateColumn()) {
      return createDateConstraint(new In<>(values.stream().map(Long.class::cast).collect(Collectors.toList())));
    } else {
      return new GenericConstraint<>(this, new In<>(values));
    }
  }

  public GenericConstraint in(MultiValue<T> multiValue) {
    return new GenericConstraint<T>(this, new In<T>(multiValue));
  }

  public GenericConstraint notIn(T value, T... otherValues) {
    if (isDateColumn()) {
      return createDateConstraint(new NotIn<>((Long)value, Arrays.copyOf(otherValues, otherValues.length, Long[].class)));
    } else {
      return new GenericConstraint<>(this, new NotIn<>(value, otherValues));
    }
  }

  public GenericConstraint notIn(Collection<T> values) {
    if (isDateColumn()) {
      return createDateConstraint(new NotIn<>(values.stream().map(Long.class::cast).collect(Collectors.toList())));
    } else {
      return new GenericConstraint<>(this, new NotIn<>(values));
    }
  }

  public GenericConstraint notIn(MultiValue<T> multiValue) {
    return new GenericConstraint<T>(this, new NotIn<T>(multiValue));
  }

  public GenericConstraint matches(String pattern) {
    return new GenericConstraint<>(this.as(String.class), new Match(pattern));
  }

  public GenericConstraint matches(Column<String> col, String prefix, String suffix) {
    return new GenericConstraint<>(this.as(String.class), new Match(col, prefix, suffix));
  }

  public GenericConstraint contains(String string) {
    return matches("%" + string + "%");
  }

  public GenericConstraint contains(Column<String> col) {
    return matches(col, "%", "%");
  }

  public GenericConstraint startsWith(String start) {
    return new GenericConstraint<>(this.as(String.class), new Match(start + "%"));
  }

  public GenericConstraint endsWith(String end) {
    return new GenericConstraint<>(this.as(String.class), new Match("%" + end));
  }

  boolean isDateColumn() {
    return java.util.Date.class.isAssignableFrom(type);
  }

  private GenericConstraint createDateConstraint(WhereOperator<Long> operator) {
    return new GenericConstraint<>(
        this.as(String.class),
        new GenericOperator<>(
            operator.getSqlStatement(),
            operator.getParameters().stream().map(JackUtility.FORMATTER_FUNCTION_MAP.get(type)).collect(Collectors.toList())
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
    return that instanceof Column && this.toString().equals(that.toString());
  }
}
