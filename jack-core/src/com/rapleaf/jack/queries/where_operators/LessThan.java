package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.SingleValue;

public class LessThan<V> extends WhereOperator<V> {

  public LessThan(V value) {
    super("< ?", value);
  }

  public LessThan(Column<V> column) {
    super("< " + column.getSqlKeyword());
    Preconditions.checkNotNull(column);
  }

  public LessThan(SingleValue<V> subQuery) {
    super("< (" + subQuery.getQueryStatement() + ")", (Collection<V>)subQuery.getParameters());
  }
}
