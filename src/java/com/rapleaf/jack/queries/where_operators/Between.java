package com.rapleaf.jack.queries.where_operators;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;

public class Between<V> extends WhereOperator<V> {

  public Between(V min, V max) {
    super("BETWEEN ? AND ?", min, max);
  }

  public Between(Column min, V max) {
    super("BETWEEN " + min.getSqlKeyword() + " AND ?", max);
    Preconditions.checkNotNull(min);
  }

  public Between(V min, Column max) {
    super("BETWEEN ? AND " + max.getSqlKeyword(), min);
    Preconditions.checkNotNull(max);
  }

  public Between(Column min, Column max) {
    super("BETWEEN " + min.getSqlKeyword() + " AND " + max.getSqlKeyword());
    Preconditions.checkNotNull(min);
    Preconditions.checkNotNull(max);
  }
}
