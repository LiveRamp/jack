package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class Between<N extends Number> extends SqlOperator<N> {

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
  public int getNbNotNullParameters() {
    return 2;
  }
}
