package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class GreaterThanOrEqualTo<N extends Comparable<N>> extends QueryOperator<N> {

  public GreaterThanOrEqualTo(N number) {
    super(number);
    if (number == null) {
      throw new IllegalArgumentException("You cannot pass null parameters.");
    }
  }

  @Override
  public String getSqlStatement() {
    return " >= ? ";
  }

  @Override
  public boolean apply(N value) {
    return value.compareTo(getParameter()) > 0;
  }

  public N getParameter() {
    return getParameters().get(0);
  }
}
