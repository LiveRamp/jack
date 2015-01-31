package com.rapleaf.jack.queries;

import com.rapleaf.jack.ModelField;

public class OrderCriterion implements IQueryCondition {
  private ModelField modelField;
  private final QueryOrder order;

  public OrderCriterion(QueryOrder order) {
    this.modelField = ModelField.field(null, null, null);
    this.order = order;
  }

  public OrderCriterion(Enum field, QueryOrder order) {
    this.modelField = ModelField.field(null, field, null);
    this.order = order;
  }

  public OrderCriterion(ModelField modelField, QueryOrder order) {
    this.modelField = modelField;
    this.order = order;
  }

  public Enum getField() {
    return modelField.getField();
  }

  public QueryOrder getOrder() {
    return order;
  }

  @Override
  public String getSqlStatement() {
    return modelField.getSqlKeyword() + " " + order.getSqlKeyword();
  }
}
