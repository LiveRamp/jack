package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class EqualTo<T> extends SqlOperator<T> {

  public EqualTo(T parameter) {
    super(parameter);
  }

  @Override
  public String getSqlStatement() {
    return getParameters().get(0) != null ? " = ? " : " IS NULL";
  }
}
