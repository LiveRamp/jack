package com.rapleaf.jack.queries;

import java.util.List;

import com.google.common.base.Optional;

import com.rapleaf.jack.Column;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class WhereConstraint<T> implements IQueryCondition {

  enum Logic {
    AND, OR
  }

  private final Column column;
  private final IWhereOperator<T> operator;
  private final Optional<Logic> logic;

  // constructor for model query
  public WhereConstraint(Enum field, IWhereOperator<T> operator) {
    this.column = Column.fromField(null, field, null);
    this.operator = operator;
    this.logic = Optional.absent();
  }

  // constructor for generic query
  public WhereConstraint(Column column, IWhereOperator<T> operator, Logic logic) {
    this.column = column;
    this.operator = operator;
    this.logic = Optional.fromNullable(logic);
  }

  public Enum getField() {
    return column.getField();
  }

  public IWhereOperator<T> getOperator() {
    return operator;
  }

  public List<T> getParameters() {
    return operator.getParameters();
  }

  @Override
  public String getSqlStatement() {
    return (logic.isPresent() ? logic.get().toString() + " " : "") + column.getSqlKeyword() + " " + operator.getSqlStatement();
  }
}
