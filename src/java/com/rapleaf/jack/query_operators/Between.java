package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class Between<N extends Comparable<N>> extends QueryOperator<N> {

  public Between(N min, N max) {
    super(min, max);
    if (min == null || max == null) {
      throw new IllegalArgumentException("You cannot pass null parameters.");
    }
  }

  @Override
  public String getSqlStatement() {
    return " BETWEEN ? AND ? ";
  }

  @Override
  public boolean apply(N value) {
    return value.compareTo(getMin()) >= 0 && value.compareTo(getMax()) <= 0;
  }

  public N getMin() {
    return getParameters().get(0);
  }

  public N getMax() {
    return getParameters().get(1);
  }
}
