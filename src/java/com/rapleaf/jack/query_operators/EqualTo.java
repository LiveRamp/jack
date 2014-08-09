package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class EqualTo<T> extends QueryOperator<T> {

  public EqualTo(T parameter) {
    super(parameter);
  }

  @Override
  public String getSqlStatement() {
    return getParameter() != null ? " = ? " : " IS NULL";
  }

  @Override
  public boolean apply(T value) {
    // If parameter is not null, call its method equals()
    if (getParameter() != null) {
      return getParameter().equals(value);
    }
    // If parameter is null, check if value is also null
    return value == null;
  }

  public T getParameter() {
    return getParameters().get(0);
  }
}
