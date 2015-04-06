package com.rapleaf.jack.queries.where_operators;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

import com.rapleaf.jack.queries.Column;

public class NotBetween<V> extends WhereOperator<V> {

  public NotBetween(V min, V max) {
    super("NOT BETWEEN ? AND ?", min, max);
  }

  public NotBetween(Column min, V max) {
    super("NOT BETWEEN " + min.getSqlKeyword() + " AND ?", max);
    Preconditions.checkNotNull(min);
  }

  public NotBetween(V min, Column max) {
    super("NOT BETWEEN ? AND " + max.getSqlKeyword(), min);
    Preconditions.checkNotNull(max);
  }

  public NotBetween(Column min, Column max) {
    super("NOT BETWEEN " + min.getSqlKeyword() + " AND " + max.getSqlKeyword());
    Preconditions.checkNotNull(min);
    Preconditions.checkNotNull(max);
  }
}
