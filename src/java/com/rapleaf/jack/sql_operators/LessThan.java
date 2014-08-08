package com.rapleaf.jack.sql_operators;

import com.sun.istack.internal.NotNull;

import com.rapleaf.jack.SqlOperator;

public class LessThan<N extends Number> extends SqlOperator<N> {

  public LessThan(@NotNull N parameter) {
    super(parameter);
  }

  @Override
  public String getSqlStatement() {
    return " < ? ";
  }
}
