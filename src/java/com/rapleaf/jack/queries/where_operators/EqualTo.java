package com.rapleaf.jack.queries.where_operators;

import com.rapleaf.jack.queries.Column;

public class EqualTo<V> extends WhereOperator<V> {

  public EqualTo(V value) {
    super("= ?", value);
    checkNotNull(value);
  }

  public EqualTo(Column column) {
    super("= " + column.getSqlKeyword());
    checkNotNull(column);
  }

  @Override
  public void ensureNoNullParameter() {
    // do nothing
  }

  public void checkNotNull(Object object) {
    if (object == null) {
      throw new IllegalArgumentException("You cannot pass a null value as a parameter. " +
          "Use the isNull operator instead, or JackMatchers.equalTo().");
    }
  }
}
