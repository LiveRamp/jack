package com.rapleaf.jack.queries.where_operators;

public class LessThanOrEqualTo<V extends Comparable<V>> extends WhereOperator<V> {

  public LessThanOrEqualTo(V value) {
    super(value);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    return "<= ?";
  }

  public V getParameter() {
    return getParameters().get(0);
  }
}
