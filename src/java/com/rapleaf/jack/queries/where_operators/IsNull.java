package com.rapleaf.jack.queries.where_operators;

public class IsNull<V> extends WhereOperator<V> {

  public IsNull() {
    super();
  }

  @Override
  public String getSqlStatement() {
    return "IS NULL";
  }
}
