package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class LessThan<N extends Number> extends SqlOperator<N> {

  public LessThan(N number) {
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
