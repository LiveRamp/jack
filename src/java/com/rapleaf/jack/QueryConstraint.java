package com.rapleaf.jack;

public class QueryConstraint<T> {
  private Enum field;
  private final ISqlOperator<T> operator;

  public QueryConstraint(ISqlOperator<T> operator, Enum field) {
    this.operator = operator;
    this.field = field;
  }

  public Enum getField() {
    return field;
  }

  public ISqlOperator<T> getOperator() {
    return operator;
  }
}
