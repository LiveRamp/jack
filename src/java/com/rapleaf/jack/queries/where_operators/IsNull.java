package com.rapleaf.jack.queries.where_operators;

public class IsNull<V> extends WhereOperator<V> {

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
