package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class IsNotNull<V> extends QueryOperator<V> {

  public IsNotNull() {
    super();
  }

  @Override
  public String getSqlStatement() {
    return " IS NOT NULL ";
  }

  @Override
  public boolean apply(V value) {
    return value != null;
  }
}
