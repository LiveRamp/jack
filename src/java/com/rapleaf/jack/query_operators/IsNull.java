package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class IsNull<V> extends QueryOperator<V> {

  public IsNull() {
    super();
  }

  @Override
  public String getSqlStatement() {
    return "IS NULL";
  }

  @Override
  public boolean apply(V value) {
    return value == null;
  }
}
