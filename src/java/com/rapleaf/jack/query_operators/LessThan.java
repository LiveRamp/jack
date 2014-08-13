package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class LessThan<V extends Comparable<V>> extends QueryOperator<V> {

  public LessThan(V value) {
    super(value);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    return "< ?";
  }

  @Override
  public boolean apply(V value) {
    return value.compareTo(getParameter()) < 0;
  }

  public V getParameter() {
    return getParameters().get(0);
  }
}
