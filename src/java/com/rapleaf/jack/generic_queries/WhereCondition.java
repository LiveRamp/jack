package com.rapleaf.jack.generic_queries;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class WhereCondition implements QueryCondition {

  private final ModelField modelField;
  private final IWhereOperator operator;

  public WhereCondition(ModelField modelField, IWhereOperator operator) {
    this.modelField = modelField;
    this.operator = operator;
  }

  @Override
  public String getSqlStatement() {
    return modelField.getSqlKeyword() + " " + operator.getSqlStatement();
  }
}
