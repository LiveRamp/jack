package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class NotEqualTo<T> extends QueryOperator<T> {

  public NotEqualTo(T parameter) {
    super(parameter);
  }

  @Override
  public String getSqlStatement() {
    return getParameter() != null ? " <> ? " : " IS NOT NULL ";
  }

  @Override
  public boolean apply(T value) {
    // If the parameter is not null, calls its method equals()
    if (getParameter() != null) {
      return !getParameter().equals(value);
    }
    // If the parameter is null, check if value is also null
    return value != null;
  }

  public T getParameter() {
    return getParameters().get(0);
  }
}
