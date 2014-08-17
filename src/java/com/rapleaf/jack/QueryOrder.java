package com.rapleaf.jack;

public abstract class QueryOrder {
  private final String queryOrder;
  
  public QueryOrder(String queryOrder) {
    this.queryOrder = queryOrder;
  }
  
  public String getSqlStatement() {
    return this.queryOrder;
  }
}