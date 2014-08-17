package com.rapleaf.jack;

public class QueryOrderConstraint {
  private Enum field;
  private final QueryOrder order;

  public QueryOrderConstraint(Enum field, QueryOrder order) {
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
    return field + " " + order.getSqlStatement();
  }
}
