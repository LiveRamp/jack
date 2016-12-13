package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

public class JackMatchers {

  public static <T> IWhereOperator<T> equalTo(T value) {
    if (value == null) {
      return new IsNull<>();
    }
    return new EqualTo<>(value);
  }

  public static <T> IWhereOperator<T> notEqualTo(T value) {
    if (value == null) {
      return new IsNotNull<>();
    }
    return new NotEqualTo<>(value);
  }

  public static <T> IsNull<T> isNull() {
    return new IsNull<>();
  }

  public static <T> IsNotNull<T> isNotNull() {
    return new IsNotNull<>();
  }

  public static <T> In<T> in(T value, T... otherValues) {
    return new In<>(value, otherValues);
  }

  public static <T> In<T> in(Collection<T> values) {
    return new In<>(values);
  }

  public static <T> NotIn<T> notIn(T value, T... otherValues) {
    return new NotIn<>(value, otherValues);
  }

  public static <T> IWhereOperator<T> notIn(Collection<T> values) {
    if (values.isEmpty()) {
      return noop();
    }
    return new NotIn<>(values);
  }

  public static <T extends Comparable<T>> GreaterThan<T> greaterThan(T value) {
    return new GreaterThan<>(value);
  }

  public static <T extends Comparable<T>> LessThan<T> lessThan(T value) {
    return new LessThan<>(value);
  }

  public static <T extends Comparable<T>> GreaterThanOrEqualTo<T> greaterThanOrEqualTo(T value) {
    return new GreaterThanOrEqualTo<>(value);
  }

  public static <T extends Comparable<T>> LessThanOrEqualTo<T> lessThanOrEqualTo(T value) {
    return new LessThanOrEqualTo<>(value);
  }

  public static <T extends Comparable<T>> Between<T> between(T min, T max) {
    return new Between<>(min, max);
  }

  public static <T extends Comparable<T>> NotBetween<T> notBetween(T min, T max) {
    return new NotBetween<>(min, max);
  }

  public static Match match(String pattern) {
    return new Match(pattern);
  }

  public static Match contains(String string) {
    return new Match("%" + string + "%");
  }

  public static Match startsWith(String start) {
    return new Match(start + "%");
  }

  public static Match endsWith(String end) {
    return new Match("%" + end);
  }

  public static <T> IWhereOperator<T> noop() {
    return new NoOp<>();
  }
}
