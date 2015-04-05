package com.rapleaf.jack.queries.where_operators;

public class NotEqualTo<V> extends WhereOperator<V> {

  public NotEqualTo(V value) {
    super("<> ?", value);
    checkNotNull(value);
  }

  public void checkNotNull(Object value) {
    if (value == null) {
      throw new IllegalArgumentException("You cannot pass a null value as a parameter. " +
          "Use the isNotNull operator instead, or JackMatchers.notEqualTo().");
    }
  }
}
