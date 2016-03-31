package com.rapleaf.jack.queries;

import java.util.List;

import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class WhereConstraint<T> implements QueryCondition {

  enum Logic {
    AND, OR
  }

  private final Column column;
  private final IWhereOperator<T> operator;
  private Logic logic;

  // constructor for model query
  public WhereConstraint(Enum field, IWhereOperator<T> operator) {
    this.column = Column.fromField(null, field, null);
    this.operator = operator;
    this.logic = null;
  }

  // constructor for generic query
  public WhereConstraint(Column column, IWhereOperator<T> operator, Logic logic) {
    this.column = column;
    this.operator = operator;
    this.logic = logic;
  }

  void setLogic(Logic logic) {
    this.logic = logic;
  }

  public Enum getField() {
    return column.getField();
  }

  public boolean isId() {
    return column.getField() == null;
  }

  public IWhereOperator<T> getOperator() {
    return operator;
  }

  public Logic getLogic() {
    return logic;
  }

  public List<T> getParameters() {
    return operator.getParameters();
  }

  @Override
  public String getSqlStatement() {
    return (logic != null ? logic.toString() + " " : "") + "`" + column.getSqlKeyword() + "` " + operator.getSqlStatement();
  }
}
