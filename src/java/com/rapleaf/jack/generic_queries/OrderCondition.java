package com.rapleaf.jack.generic_queries;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.queries.QueryOrder;

public class OrderCondition implements QueryCondition {

  private final ModelField modelField;
  private final QueryOrder queryOrder;

  public OrderCondition(ModelField modelField, QueryOrder queryOrder) {
    this.modelField = modelField;
    this.queryOrder = queryOrder;
  }

  @Override
  public String getSqlStatement() {
    return modelField.getSqlKeyword() + " " + queryOrder.getSqlKeyword();
  }
}
