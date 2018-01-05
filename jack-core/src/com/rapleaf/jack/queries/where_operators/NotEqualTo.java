package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.SingleValue;

public class NotEqualTo<V> extends WhereOperator<V> {

  public NotEqualTo(V value) {
    super("<> ?", value);
    try {
      ensureNoNullParameter();
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("You cannot pass a null value as a parameter. " +
          "Use the isNotNull operator instead, or JackMatchers.notEqualTo().");
    }
  }

  public NotEqualTo(Column<V> column) {
    super("<> " + column.getSqlKeyword());
    Preconditions.checkNotNull(column);
  }

  public NotEqualTo(SingleValue<V> subQuery) {
    super("<> (" + subQuery.getQueryStatement() + ")", (Collection<V>)subQuery.getParameters());
  }
}
