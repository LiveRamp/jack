package com.rapleaf.jack;

import com.rapleaf.jack.sql_operators.Between;
import com.rapleaf.jack.sql_operators.GreaterThan;
import com.rapleaf.jack.sql_operators.GreaterThanOrEqualTo;
import com.rapleaf.jack.sql_operators.LessThan;
import com.rapleaf.jack.sql_operators.LessThanOrEqualTo;
import com.rapleaf.jack.sql_operators.Match;

public class JackMatchers {

  public static <T> void equalto();

  public static <T> void notEqualto();

  public static <T> void in();

  public static <T> void notIn();

  public static <N extends Number> GreaterThan<N> greaterThan(N number);

  public static <N extends Number> LessThan<N> lessThan(N number);

  public static <N extends Number> GreaterThanOrEqualTo<N> greaterThanOrEqualTo(N number);

  public static <N extends Number> LessThanOrEqualTo<N> lessThanOrEqualto(N number);

  public static <N extends Number> Between<N> between(N min, N max);

  public static Match match();

  public static Match contains();

  public static Match startsWith();

  public static Match endsWith();

  public static Match contains();


}
