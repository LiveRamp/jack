package com.rapleaf.jack.sql_operators;

import com.sun.istack.internal.NotNull;

import com.rapleaf.jack.SqlOperator;

public class LessThanOrEqualTo<N extends Number> extends SqlOperator<N> {

  public LessThanOrEqualTo(@NotNull N number) {
    super(number);
    if (number == null) {
      throw new IllegalArgumentException("You cannot pass null parameters.");
    }
  }

  @Override
  public String getSqlStatement() {
    return " < ? ";
  }

  @Override
  public int getNbNotNullParameters() {
    return 1;
  }
}
