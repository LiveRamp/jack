package com.rapleaf.jack;

public class QueryConstraint<T> {
  private Enum field;
  private final ISqlOperator<T> operator;

  public QueryConstraint(Enum field, ISqlOperator<T> operator) {
    this.field = field;
    this.operator = operator;
  }

  public Enum getField() {
    return field;
  }

  public ISqlOperator<T> getOperator() {
    return operator;
  }
}
