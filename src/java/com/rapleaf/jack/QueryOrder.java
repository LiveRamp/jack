package com.rapleaf.jack;

public enum QueryOrder {
  ASC("ASC"), DESC("DESC");
  private String queryOrder;

  private QueryOrder(String order) {
    this.queryOrder = order;
  }
  
  public String getSqlStatement() {
    return this.queryOrder;
  }
}
