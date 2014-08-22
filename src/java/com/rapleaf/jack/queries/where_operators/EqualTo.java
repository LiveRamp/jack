package com.rapleaf.jack.queries.where_operators;

public class EqualTo<V> extends WhereOperator<V> {

  public EqualTo(V value) {
    super(value);
    try {
      ensureNoNullParameter();
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("You cannot pass a null value as a parameter. " +
          "Use the EqualToOrNull operator instead.");
    }
  }

  @Override
  public String getSqlStatement() {
    return "= ?";
  }

  @Override
  public boolean apply(V value) {
    return getSingleParameter().equals(value);
  }
}
