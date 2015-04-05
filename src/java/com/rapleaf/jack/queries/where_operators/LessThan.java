package com.rapleaf.jack.queries.where_operators;

public class LessThan<V> extends WhereOperator<V> {

  public LessThan(V value) {
    super("< ?", value);
  }
}
