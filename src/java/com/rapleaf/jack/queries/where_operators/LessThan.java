package com.rapleaf.jack.queries.where_operators;

public class LessThan<V extends Comparable<V>> extends WhereOperator<V> {

  public LessThan(V value) {
    super(value);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    return "< ?";
  }

  public V getParameter() {
    return getParameters().get(0);
  }
}
