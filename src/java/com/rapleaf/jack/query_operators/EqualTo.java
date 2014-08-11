package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class EqualTo<V> extends QueryOperator<V> {

  public EqualTo(V value) {
    super(value);
  }

  @Override
  public String getSqlStatement() {
    return getSingleParameter() != null ? " = ? " : " IS NULL";
  }

  @Override
  public boolean apply(V value) {
    // If parameter is not null, call its method equals()
    if (getSingleParameter() != null) {
      return getSingleParameter().equals(value);
    }
    // If parameter is null, check if value is also null
    return value == null;
  }
}
