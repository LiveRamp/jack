package com.rapleaf.jack.generic_queries;

import com.rapleaf.jack.IModelField;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class WhereCondition implements QueryCondition {

  private final IModelField IModelField;
  private final IWhereOperator operator;

  public WhereCondition(IModelField IModelField, IWhereOperator operator) {
    this.IModelField = IModelField;
    this.operator = operator;
  }

  @Override
  public String getSqlStatement() {
    return IModelField.getSqlKeyword() + " " + operator.getSqlStatement();
  }
}
