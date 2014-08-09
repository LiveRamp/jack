package com.rapleaf.jack;

import com.rapleaf.jack.query_operators.Between;
import com.rapleaf.jack.query_operators.EqualTo;
import com.rapleaf.jack.query_operators.GreaterThan;
import com.rapleaf.jack.query_operators.GreaterThanOrEqualTo;
import com.rapleaf.jack.query_operators.In;
import com.rapleaf.jack.query_operators.LessThan;
import com.rapleaf.jack.query_operators.LessThanOrEqualTo;
import com.rapleaf.jack.query_operators.Match;
import com.rapleaf.jack.query_operators.NotEqualTo;
import com.rapleaf.jack.query_operators.NotIn;

public class JackMatchers {

  public static <T> EqualTo<T> equalTo(T value) {
    return new EqualTo<T>(value);
  }

  public static <T> NotEqualTo<T> notEqualTo(T value) {
    return new NotEqualTo<T>(value);
  }

  public static <T> In<T> in(T... values) {
    return new In<T>(values);
  }

  public static <T> NotIn<T> notIn(T... values) {
    return new NotIn<T>(values);
  }

  public static <N extends Comparable<N>> GreaterThan<N> greaterThan(N number) {
    return new GreaterThan<N>(number);
  }

  public static <N extends Comparable<N>> LessThan<N> lessThan(N number) {
    return new LessThan<N>(number);
  }

  public static <N extends Comparable<N>> GreaterThanOrEqualTo<N> greaterThanOrEqualTo(N number) {
    return new GreaterThanOrEqualTo<N>(number);
  }

  public static <N extends Comparable<N>> LessThanOrEqualTo<N> lessThanOrEqualto(N number) {
    return new LessThanOrEqualTo<N>(number);
  }

  public static <N extends Comparable<N>> Between<N> between(N min, N max) {
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
