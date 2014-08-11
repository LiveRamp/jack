package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class NotEqualTo<V> extends QueryOperator<V> {

  public NotEqualTo(V value) {
    super(value);
  }

  @Override
  public String getSqlStatement() {
    return getSingleParameter() != null ? " <> ? " : " IS NOT NULL ";
  }

  @Override
  public boolean apply(V value) {
    // If the parameter is not null, calls its method equals()
    if (getSingleParameter() != null) {
      return !getSingleParameter().equals(value);
    }
    // If the parameter is null, check if value is also null
    return value != null;
  }
}
