package com.rapleaf.jack.queries.where_operators;

import com.rapleaf.jack.queries.Column;

public class GreaterThanOrEqualTo<V> extends WhereOperator<V> {

  public GreaterThanOrEqualTo(V value) {
    super(">= ?", value);
  }

  public GreaterThanOrEqualTo(Column column) {
    super(">= " + column.getSqlKeyword());
  }
}
