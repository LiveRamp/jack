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

  public V getMin() {
    return getParameters().get(0);
  }

  public V getMax() {
    return getParameters().get(1);
  }
}
