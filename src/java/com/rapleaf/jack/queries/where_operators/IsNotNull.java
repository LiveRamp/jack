package com.rapleaf.jack.queries.where_operators;

public class IsNotNull<V> extends WhereOperator<V> {

  public IsNotNull() {
    super();
  }

  @Override
  public String getSqlStatement() {
    return "IS NOT NULL";
  }

  @Override
  public boolean apply(V value) {
    return value != null;
  }
}
