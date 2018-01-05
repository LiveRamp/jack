package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.SingleValue;

public class LessThanOrEqualTo<V> extends WhereOperator<V> {

  public LessThanOrEqualTo(V value) {
    super("<= ?", value);
  }

  public LessThanOrEqualTo(Column<V> column) {
    super("<= " + column.getSqlKeyword());
  }

  public LessThanOrEqualTo(SingleValue subQuery) {
    super("<= (" + subQuery.getQueryStatement() + ")", (Collection<V>)subQuery.getParameters());
  }
}
