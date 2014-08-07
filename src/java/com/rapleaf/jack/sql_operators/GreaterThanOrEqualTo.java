package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class GreaterThanOrEqualTo<N extends Number> extends SqlOperator<N> {

  public GreaterThanOrEqualTo(N number) {
    super(number);
  }

  @Override
  public String getSqlStatement() {
    return " >= ? ";
  }
}
