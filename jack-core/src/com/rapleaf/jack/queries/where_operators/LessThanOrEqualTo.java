package com.rapleaf.jack.queries.where_operators;

import com.rapleaf.jack.queries.Column;

public class LessThanOrEqualTo<V> extends WhereOperator<V> {

  public LessThanOrEqualTo(V value) {
    super("<= ?", value);
  }

  public LessThanOrEqualTo(Column<V> column) {
    super("<= " + column.getSqlKeyword());
  }
}
