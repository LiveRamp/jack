package com.rapleaf.jack.queries;

import java.util.List;

import com.google.common.base.Optional;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class WhereConstraint<T> implements IQueryCondition {

  enum Logic {
    AND, OR
  }

  private final ModelField modelField;
  private final IWhereOperator<T> operator;
  private final Optional<Logic> logic;

  // constructor for model query
  public WhereConstraint(Enum field, IWhereOperator<T> operator) {
    this.modelField = ModelField.field(null, field, null);
    this.operator = operator;
    this.logic = Optional.absent();
  }

  // constructor for generic query
  public WhereConstraint(ModelField modelField, IWhereOperator<T> operator, Logic logic) {
    this.modelField = modelField;
    this.operator = operator;
    this.logic = Optional.fromNullable(logic);
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
    return (logic.isPresent() ? logic.toString() + " " : "") + modelField.getSqlKeyword() + " " + operator.getSqlStatement();
  }
}
