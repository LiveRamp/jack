package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class GreaterThan<N extends Number> extends SqlOperator<N> {

  public GreaterThan(N number) {
    super(number);
  }

  @Override
  public String getSqlStatement() {
    return " >= ? ";
  }
}
