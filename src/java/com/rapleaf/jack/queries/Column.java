package com.rapleaf.jack.queries;

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
import com.rapleaf.jack.queries.where_operators.NotEqualTo;
import com.rapleaf.jack.queries.where_operators.NotIn;

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
    return new GenericConstraint<T>(this, new EqualTo<T>(value));
  }

  public <T> GenericConstraint<T> notEqualTo(T value) {
    return new GenericConstraint<T>(this, new NotEqualTo<T>(value));
  }

  public <T extends Comparable<T>> GenericConstraint<T> greaterThan(T value) {
    return new GenericConstraint<T>(this, new GreaterThan<T>(value));
  }

  public <T extends Comparable<T>> GenericConstraint<T> greaterThanOrEqualTo(T value) {
    return new GenericConstraint<T>(this, new GreaterThanOrEqualTo<T>(value));
  }

  public <T extends Comparable<T>> GenericConstraint<T> lessThan(T value) {
    return new GenericConstraint<T>(this, new LessThan<T>(value));
  }

  public <T extends Comparable<T>> GenericConstraint<T> lessThanOrEqualTo(T value) {
    return new GenericConstraint<T>(this, new LessThanOrEqualTo<T>(value));
  }

  public <T extends Comparable<T>> GenericConstraint<T> between (T min, T max) {
    return new GenericConstraint<T>(this, new Between<T>(min, max));
  }

  public <T> GenericConstraint<T> in(T value, T... otherValues) {
    return new GenericConstraint<T>(this, new In<T>(value, otherValues));
  }

  public <T> GenericConstraint<T> notIn(T value, T... otherValues) {
    return new GenericConstraint<T>(this, new NotIn<T>(value, otherValues));
  }

  public GenericConstraint<String> match(String pattern) {
    return new GenericConstraint<String>(this, new Match(pattern));
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
