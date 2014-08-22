package com.rapleaf.jack.queries;

import java.util.List;

import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class WhereConstraint<T> {
  private Enum field;
  private final IWhereOperator<T> operator;

  public WhereConstraint(Enum field, IWhereOperator<T> operator) {
    this.field = field;
    this.operator = operator;
  }

  public Enum getField() {
    return field;
  }

  public IWhereOperator<T> getOperator() {
    return operator;
  }

  public List<T> getParameters() {
    return operator.getParameters();
  }

  public String getSqlStatement() {
    return field + " " + operator.getSqlStatement();
  }
}
