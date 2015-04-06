package com.rapleaf.jack.queries.where_operators;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;

public class LessThan<V> extends WhereOperator<V> {

  public LessThan(V value) {
    super("< ?", value);
  }

  public LessThan(Column column) {
    super("< " + column.getSqlKeyword());
    Preconditions.checkNotNull(column);
  }
}
