package com.rapleaf.jack.queries.where_operators;

public class Between<V extends Comparable<V>> extends WhereOperator<V> {

  public Between(V min, V max) {
    super(min, max);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    return "BETWEEN ? AND ?";
  }

  @Override
  public boolean apply(V value) {
    return value.compareTo(getMin()) >= 0 && value.compareTo(getMax()) <= 0;
  }

  public V getMin() {
    return getParameters().get(0);
  }

  public V getMax() {
    return getParameters().get(1);
  }
}
