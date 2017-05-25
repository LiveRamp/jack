package com.rapleaf.jack.queries.where_operators;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;

public class NotBetween<V> extends WhereOperator<V> {

  public NotBetween(V min, V max) {
    super("NOT BETWEEN ? AND ?", min, max);
  }

  public NotBetween(Column<V> min, V max) {
    super("NOT BETWEEN " + min.getSqlKeyword() + " AND ?", max);
    Preconditions.checkNotNull(min);
  }

  public NotBetween(V min, Column<V> max) {
    super("NOT BETWEEN ? AND " + max.getSqlKeyword(), min);
    Preconditions.checkNotNull(max);
  }

  public NotBetween(Column min, Column<V> max) {
    super("NOT BETWEEN " + min.getSqlKeyword() + " AND " + max.getSqlKeyword());
    Preconditions.checkNotNull(min);
    Preconditions.checkNotNull(max);
  }
}
