package com.rapleaf.jack;

import com.rapleaf.jack.query_operators.Between;
import com.rapleaf.jack.query_operators.EqualTo;
import com.rapleaf.jack.query_operators.GreaterThan;
import com.rapleaf.jack.query_operators.GreaterThanOrEqualTo;
import com.rapleaf.jack.query_operators.In;
import com.rapleaf.jack.query_operators.IsNotNull;
import com.rapleaf.jack.query_operators.IsNull;
import com.rapleaf.jack.query_operators.LessThan;
import com.rapleaf.jack.query_operators.LessThanOrEqualTo;
import com.rapleaf.jack.query_operators.Match;
import com.rapleaf.jack.query_operators.NotEqualTo;
import com.rapleaf.jack.query_operators.NotIn;

public class JackMatchers {

  public static <T> IQueryOperator<T> equalToOrNull(T value) {
    if (value == null) {
      return new IsNull<T>();
    }
    return new EqualTo<T>(value);
  }

  public static <T> IQueryOperator<T> notEqualToOrNotNull(T value) {
    if (value == null) {
      return new IsNotNull<T>();
    }
    return new NotEqualTo<T>(value);
  }

  //To change?? NO!!
  public static <T> EqualTo<T> equalTo(T value) {
    return new EqualTo<T>(value);
  }

  public static <T> NotEqualTo<T> notEqualTo(T value) {
    return new NotEqualTo<T>(value);
  }

  public static <T> IsNull<T> isNull() {
    return new IsNull<T>();
  }

  public static <T> IsNotNull<T> isNotNull() {
    return new IsNotNull<T>();
  }

  public static <T> In<T> in(T value1, T... otherValues) {
    return new In<T>(value1, otherValues);
  }

  public static <T> NotIn<T> notIn(T value1, T... otherValues) {
    return new NotIn<T>(value1, otherValues);
  }

  public static <T extends Comparable<T>> GreaterThan<T> greaterThan(T value) {
    return new GreaterThan<T>(value);
  }

  public static <T extends Comparable<T>> LessThan<T> lessThan(T value) {
    return new LessThan<T>(value);
  }

  public static <T extends Comparable<T>> GreaterThanOrEqualTo<T> greaterThanOrEqualTo(T value) {
    return new GreaterThanOrEqualTo<T>(value);
  }

  public static <T extends Comparable<T>> LessThanOrEqualTo<T> lessThanOrEqualTo(T value) {
    return new LessThanOrEqualTo<T>(value);
  }

  public static <T extends Comparable<T>> Between<T> between(T min, T max) {
    return new Between<T>(min, max);
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
}
