package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class EqualTo<T> extends QueryOperator<T> {

  public EqualTo(T parameter) {
    super(parameter);
  }

  @Override
  public String getSqlStatement() {
    return getSingleParameter() != null ? " = ? " : " IS NULL";
  }

  @Override
  public boolean apply(T value) {
    // If parameter is not null, call its method equals()
    if (getSingleParameter() != null) {
      return getSingleParameter().equals(value);
    }
    // If parameter is null, check if value is also null
    return value == null;
  }
}
