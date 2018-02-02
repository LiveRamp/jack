package com.rapleaf.jack.queries.where_operators;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;

public class Match extends WhereOperator<String> {

  public Match(String pattern) {
    super("LIKE ?", pattern);
  }

  public Match(Column<String> column, String prefix, String suffix) {
    super(String.format("LIKE CONCAT(CONCAT('%s', %s), '%s')", prefix, Preconditions.checkNotNull(column).getSqlKeyword(), suffix));
  }
}
