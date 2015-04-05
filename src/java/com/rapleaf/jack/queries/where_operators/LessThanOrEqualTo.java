package com.rapleaf.jack.queries.where_operators;

public class LessThanOrEqualTo<V> extends WhereOperator<V> {

  public LessThanOrEqualTo(V value) {
    super("<= ?", value);
  }
}
