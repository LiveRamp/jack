package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.SingleValue;

public class GreaterThan<V> extends WhereOperator<V> {

  public GreaterThan(V value) {
    super("> ?", value);
  }

  public GreaterThan(Column<V> column) {
    super("> " + column.getSqlKeyword());
    Preconditions.checkNotNull(column);
  }

  public GreaterThan(SingleValue<V> subQuery) {
    super("> (" + subQuery.getQueryStatement() + ")", (Collection<V>)subQuery.getParameters());
  }
}
