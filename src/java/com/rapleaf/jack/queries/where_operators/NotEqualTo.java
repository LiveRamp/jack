package com.rapleaf.jack.queries.where_operators;

import com.rapleaf.jack.queries.Column;

public class NotEqualTo<V> extends WhereOperator<V> {

  public NotEqualTo(V value) {
    super("<> ?", value);
    checkNotNull(value);
  }

  public NotEqualTo(Column column) {
    super("<> " + column.getSqlKeyword());
    checkNotNull(column);
  }

  @Override
  public void ensureNoNullParameter() {
    // do nothing
  }

  public void checkNotNull(Object value) {
    if (value == null) {
      throw new IllegalArgumentException("You cannot pass a null value as a parameter. " +
          "Use the isNotNull operator instead, or JackMatchers.notEqualTo().");
    }
  }
}
