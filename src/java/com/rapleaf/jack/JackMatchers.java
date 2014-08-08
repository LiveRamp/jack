package com.rapleaf.jack;

import com.rapleaf.jack.sql_operators.Between;
import com.rapleaf.jack.sql_operators.EqualTo;
import com.rapleaf.jack.sql_operators.GreaterThan;
import com.rapleaf.jack.sql_operators.GreaterThanOrEqualTo;
import com.rapleaf.jack.sql_operators.In;
import com.rapleaf.jack.sql_operators.LessThan;
import com.rapleaf.jack.sql_operators.LessThanOrEqualTo;
import com.rapleaf.jack.sql_operators.Match;
import com.rapleaf.jack.sql_operators.NotEqualTo;
import com.rapleaf.jack.sql_operators.NotIn;

public class JackMatchers {

  public static <T> EqualTo<T> equalto(T value) {
    return new EqualTo<T>(value);
  }

  public static <T> NotEqualTo<T> notEqualto(T value) {
    return new NotEqualTo<T>(value);
  }

  public static <T> In<T> in(T... values) {
    return new In<T>(values);
  }

  public static <T> NotIn<T> notIn(T... values) {
    return new NotIn<T>(values);
  }

  public static <N extends Number> GreaterThan<N> greaterThan(N number) {
    return new GreaterThan<N>(number);
  }

  public static <N extends Number> LessThan<N> lessThan(N number) {
    return new LessThan<N>(number);
  }

  public static <N extends Number> GreaterThanOrEqualTo<N> greaterThanOrEqualTo(N number) {
    return new GreaterThanOrEqualTo<N>(number);
  }

  public static <N extends Number> LessThanOrEqualTo<N> lessThanOrEqualto(N number) {
    return new LessThanOrEqualTo<N>(number);
  }

  public static <N extends Number> Between<N> between(N min, N max) {
    return new Between<N>(min, max);
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
