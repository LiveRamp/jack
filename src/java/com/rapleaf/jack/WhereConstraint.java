package com.rapleaf.jack;

import java.util.List;

public class WhereConstraint<T> {
  private Enum field;
  private final IQueryOperator<T> operator;

  public WhereConstraint(Enum field, IQueryOperator<T> operator) {
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
