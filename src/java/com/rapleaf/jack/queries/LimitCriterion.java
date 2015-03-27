package com.rapleaf.jack.queries;
    
public class LimitCriterion implements QueryCondition {
  private int offset;
  private int nResults;

  public LimitCriterion(int offset, int nResults) {
    this.offset = offset;
    this.nResults = nResults;
  }

  public LimitCriterion(int nResults) {
    this.offset = 0;
    this.nResults = nResults;
  }

  public int getOffset() {
    return offset;
  }

  public int getNResults() {
    return nResults;
  }

  @Override
  public String getSqlStatement() {
    return "LIMIT " + nResults + " OFFSET " + offset;
  }
}
