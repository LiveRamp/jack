package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class NotEqualTo<T> extends SqlOperator<T> {

  public NotEqualTo(T parameter) {
    super(parameter);
  }

  @Override
  public String getSqlStatement() {
    return getSingleParameter() != null ? " <> ? " : " IS NOT NULL ";
  }

  @Override
  public int getNbNotNullParameters() {
    return getSingleParameter() != null ? 1 : 0;
  }

  public T getSingleParameter() {
    return getParameters().get(0);
  }
}
