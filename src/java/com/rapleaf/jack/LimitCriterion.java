package com.rapleaf.jack;

public class LimitCriterion {
  private int offset;
  private int numberOfResults;

  public LimitCriterion(int offset, int numberOfResults) {
    this.offset = offset;
    this.numberOfResults = numberOfResults;
  }
  public LimitCriterion(int numberOfResults) {
    this.offset = 0;
    this.numberOfResults = numberOfResults;
  }

  public String getSqlKeyword() {
    return "LIMIT " + offset + ", " + numberOfResults;
  }
}
