package com.rapleaf.jack.queries;

import java.util.List;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class WhereConstraint<T> implements QueryCondition {
  private ModelField modelField;
  private final IWhereOperator<T> operator;

  public WhereConstraint(Enum field, IWhereOperator<T> operator) {
    this.modelField = ModelField.field(null, field, null);
    this.operator = operator;
  }

  public WhereConstraint(ModelField modelField, IWhereOperator<T> operator) {
    this.modelField = modelField;
    this.operator = operator;
  }

  public Enum getField() {
    return modelField.getField();
  }

  public IWhereOperator<T> getOperator() {
    return operator;
  }

  public List<T> getParameters() {
    return operator.getParameters();
  }

  @Override
  public String getSqlStatement() {
    return modelField.getSqlKeyword() + " " + operator.getSqlStatement();
  }
}
