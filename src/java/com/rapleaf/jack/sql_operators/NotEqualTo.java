package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class NotEqualTo<T> extends SqlOperator<T> {

  public NotEqualTo(T parameter) {
    super(parameter);
  }

  @Override
  public String getSqlStatement() {
    return getParameter() != null ? " <> ? " : " IS NOT NULL ";
  }

  @Override
  public boolean apply(T value) {
    return value != getParameter();
  }

  @Override
  public int getNbNotNullParameters() {
    return getParameter() != null ? 1 : 0;
  }

  public T getParameter() {
    return getParameters().get(0);
  }
}
