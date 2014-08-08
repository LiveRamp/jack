package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class Between<N extends Number> extends SqlOperator<N> {

  public Between(N param1, N param2) {
    super(param1, param2);
    if (param1 == null || param2 == null) {
      throw new IllegalArgumentException("You cannot pass null parameters.");
    }
  }

  @Override
  public String getSqlStatement() {
    return " BETWEEN ? AND ? ";
  }
}
