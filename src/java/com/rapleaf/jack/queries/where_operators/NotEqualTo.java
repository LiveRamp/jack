package com.rapleaf.jack.queries.where_operators;

public class NotEqualTo<V> extends WhereOperator<V> {

  public NotEqualTo(V value) {
    super(value);
    try {
      ensureNoNullParameter();
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("You cannot pass a null value as a parameter. " +
          "Use the isNotNull operator instead, or JackMatchers.notEqualTo().");
    }
  }

  @Override
  public String getSqlStatement() {
    return "<> ?";
  }

  @Override
  public boolean apply(V value) {
    return !getSingleParameter().equals(value);
  }
}
