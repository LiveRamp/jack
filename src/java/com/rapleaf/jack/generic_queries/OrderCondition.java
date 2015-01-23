package com.rapleaf.jack.generic_queries;

import com.rapleaf.jack.IModelField;
import com.rapleaf.jack.queries.QueryOrder;

public class OrderCondition implements QueryCondition {

  private final IModelField IModelField;
  private final QueryOrder queryOrder;

  public OrderCondition(IModelField IModelField, QueryOrder queryOrder) {
    this.IModelField = IModelField;
    this.queryOrder = queryOrder;
  }

  @Override
  public String getSqlStatement() {
    return IModelField.getSqlKeyword() + " " + queryOrder.getSqlKeyword();
  }
}
