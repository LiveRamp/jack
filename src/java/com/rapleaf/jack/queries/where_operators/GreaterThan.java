package com.rapleaf.jack.queries.where_operators;

public class GreaterThan<V extends Comparable<V>> extends WhereOperator<V> {

  public GreaterThan(V value) {
    super(value);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    return "> ?";
  }
}
