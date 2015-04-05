package com.rapleaf.jack.queries.where_operators;

import com.rapleaf.jack.queries.Column;

public class Between<V> extends WhereOperator<V> {

  public Between(V min, V max) {
    super("BETWEEN ? AND ?", min, max);
  }

  public Between(Column column, V max) {
    super("BETWEEN " + column.getSqlKeyword() + " AND ?", max);
  }

  public Between(V min, Column column) {
    super("BETWEEN ? AND " + column.getSqlKeyword(), min);
  }

  public Between(Column column1, Column column2) {
    super("BETWEEN " + column1.getSqlKeyword() + " AND " + column2.getSqlKeyword());
  }
}
