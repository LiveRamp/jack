package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class EqualTo<T> extends SqlOperator<T> {

  @Override
  public String getSqlStatement() {
    return " = ? ";
  }
}
