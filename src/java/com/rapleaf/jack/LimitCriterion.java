package com.rapleaf.jack;
    
  public class LimitCriterion {
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

    public String getSqlClause() {
      return "LIMIT " + nResults + " OFFSET " + offset + "";
    }
  }