package com.rapleaf.jack.sql_operators;

import com.sun.istack.internal.NotNull;

import com.rapleaf.jack.SqlOperator;

public class LessThanOrEqualTo<N extends Number> extends SqlOperator<N> {

  public LessThanOrEqualTo(@NotNull N parameter) {
    super(parameter);
  }

  @Override
  public String getSqlStatement() {
    return " < ? ";
  }
}
