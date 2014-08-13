package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class GreaterThanOrEqualTo<V extends Comparable<V>> extends QueryOperator<V> {

  public GreaterThanOrEqualTo(V value) {
    super(value);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    return ">= ?";
  }

  @Override
  public boolean apply(V value) {
    return value.compareTo(getSingleParameter()) >= 0;
  }
}
