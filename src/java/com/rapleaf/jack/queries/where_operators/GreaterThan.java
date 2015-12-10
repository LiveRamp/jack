package com.rapleaf.jack.queries.where_operators;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;

public class GreaterThan<V> extends WhereOperator<V> {

  public GreaterThan(V value) {
    super("> ?", value);
  }

  public GreaterThan(Column<V> column) {
    super("> " + column.getSqlKeyword());
    Preconditions.checkNotNull(column);
  }
}
