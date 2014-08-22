package com.rapleaf.jack.queries.where_operators;

public class GreaterThanOrEqualTo<V extends Comparable<V>> extends WhereOperator<V> {

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
