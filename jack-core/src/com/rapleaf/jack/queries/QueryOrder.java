package com.rapleaf.jack.queries;

public enum QueryOrder {
  ASC, DESC;
  
  public String getSqlKeyword() {
    return this.toString();
  }
}
