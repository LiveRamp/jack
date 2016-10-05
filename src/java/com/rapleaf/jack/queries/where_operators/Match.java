package com.rapleaf.jack.queries.where_operators;

import com.google.common.base.Preconditions;
import com.rapleaf.jack.queries.Column;

public class Match extends WhereOperator<String> {

  public Match(String pattern) {
    super("LIKE ?", pattern);
  }

  public Match(Column<String> column, String prefix, String suffix) {
    super(String.format("LIKE CONCAT('%s', %s, '%s')", prefix, column.getSqlKeyword(), suffix));
    Preconditions.checkNotNull(column);
  }
}
