package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class Between<N extends Number> extends SqlOperator<N> {

  public Between(N param1, N param2) {
    super(param1, param2);
  }

  @Override
  public String getSqlStatement() {
    return " BETWEEN ? AND ? ";
  }
}
