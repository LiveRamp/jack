package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.SingleValue;

public class EqualTo<V> extends WhereOperator<V> {

  public EqualTo(V value) {
    super("= ?", value);
    try {
      ensureNoNullParameter();
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("You cannot pass a null value as a parameter. " +
          "Use the isNull operator instead, or JackMatchers.equalTo().");
    }
  }

  public EqualTo(Column<V> column) {
    super("= " + column.getSqlKeyword());
    Preconditions.checkNotNull(column);
  }

  public EqualTo(SingleValue subQuery) {
    super("= (" + subQuery.getQueryStatement() + ")", (Collection<V>)subQuery.getParameters());
  }
}
