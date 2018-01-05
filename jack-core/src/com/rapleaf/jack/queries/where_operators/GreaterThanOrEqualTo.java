package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.SingleValue;

public class GreaterThanOrEqualTo<V> extends WhereOperator<V> {

  public GreaterThanOrEqualTo(V value) {
    super(">= ?", value);
  }

  public GreaterThanOrEqualTo(Column<V> column) {
    super(">= " + column.getSqlKeyword());
  }

  public GreaterThanOrEqualTo(SingleValue subQuery) {
    super(">= (" + subQuery.getQueryStatement() + ")", (Collection<V>)subQuery.getParameters());
  }
}
