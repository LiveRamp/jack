package com.rapleaf.jack.generic_queries;

import java.util.List;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class WhereCondition implements QueryCondition {

  private final ModelField modelField;
  private final IWhereOperator operator;

  public WhereCondition(ModelField modelField, IWhereOperator operator) {
    this.modelField = modelField;
    this.operator = operator;
  }

  public Class getModelFieldType() {
    return modelField.getType();
  }

  public List getParameters() {
    return operator.getParameters();
  }

  @Override
  public String getSqlStatement() {
    return modelField.getFullSqlKeyword() + " " + operator.getSqlStatement();
  }
}
