package com.rapleaf.jack;

import java.util.List;

public class QueryConstraint<T> {
  private Enum field;
  private final IQueryOperator<T> operator;

  public QueryConstraint(Enum field, IQueryOperator<T> operator) {
    this.field = field;
    this.operator = operator;
  }

  public Enum getField() {
    return field;
  }

  public IQueryOperator<T> getOperator() {
    return operator;
  }

  public List<T> getParameters() {
    return operator.getParameters();
  }

  public String getSqlStatement() {
    return field + " " + operator.getSqlStatement();
  }
}
