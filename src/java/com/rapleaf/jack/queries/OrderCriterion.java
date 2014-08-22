package com.rapleaf.jack.queries;

public class OrderCriterion {
  private Enum field;
  private final QueryOrder order;

  public OrderCriterion(Enum field, QueryOrder order) {
    this.field = field;
    this.order = order;
  }

  public Enum getField() {
    return field;
  }

  public QueryOrder getOrder() {
    return order;
  }

  public String getSqlStatement() {
    return (field == null ? "id" : field) + " " + order.getSqlKeyword();
  }
}
