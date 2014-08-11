package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class NotEqualTo<V> extends QueryOperator<V> {

  public NotEqualTo(V value) {
    super(value);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    return " <> ? ";
  }

  @Override
  public boolean apply(V value) {
    return !getSingleParameter().equals(value);
  }
}
