package com.rapleaf.jack.queries;

public enum QueryOrder {
  ASC("ASC"), DESC("DESC");
  private String queryOrder;

  private QueryOrder(String order) {
    this.queryOrder = order;
  }
  
  public String getSqlKeyword() {
    return this.queryOrder;
  }
}
