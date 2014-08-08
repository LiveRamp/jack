package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class NotEqualTo<T> extends SqlOperator<T> {

  public NotEqualTo(T parameter) {
    super(parameter);
  }

  @Override
  public String getSqlStatement() {
    return getParameters().get(0) != null ? " <> ? " : " IS NOT NULL ";
  }
}
