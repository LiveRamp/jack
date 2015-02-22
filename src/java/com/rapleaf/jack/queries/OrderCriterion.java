package com.rapleaf.jack.queries;

import com.rapleaf.jack.Column;

public class OrderCriterion implements IQueryCondition {
  private Column column;
  private final QueryOrder order;

  public OrderCriterion(QueryOrder order) {
    this.column = Column.fromField(null, null, null);
    this.order = order;
  }

  public OrderCriterion(Enum field, QueryOrder order) {
    this.column = Column.fromField(null, field, null);
    this.order = order;
  }

  public OrderCriterion(Column column, QueryOrder order) {
    this.column = column;
    this.order = order;
  }

  public Enum getField() {
    return column.getField();
  }

  public QueryOrder getOrder() {
    return order;
  }

  @Override
  public String getSqlStatement() {
    return column.getSqlKeyword() + " " + order.getSqlKeyword();
  }
}
