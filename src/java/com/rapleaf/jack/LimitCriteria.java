package com.rapleaf.jack;

public class LimitCriteria {
  private int offset;
  private int numberOfResults;

  public LimitCriteria(int offset, int numberOfResults) {
    this.offset = offset;
    this.numberOfResults = numberOfResults;
  }
  public LimitCriteria(int numberOfResults) {
    this.offset = 0;
    this.numberOfResults = numberOfResults;
  }

  public String getSql() {
    return "LIMIT " + offset + ", " + numberOfResults;
  }
}
