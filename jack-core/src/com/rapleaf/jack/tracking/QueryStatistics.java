package com.rapleaf.jack.tracking;

public class QueryStatistics {
  public final long executionTimeNanos;
  public final long queryPrepTimeNanos;
  public final long numTries;

  public QueryStatistics(long executionTimeNanos, long queryPrepTimeNanos, long numTries) {
    this.executionTimeNanos = executionTimeNanos;
    this.queryPrepTimeNanos = queryPrepTimeNanos;
    this.numTries = numTries;
  }

  public static class Measurer {

    private long queryPrepStart;
    private long queryPrepEnd;
    private long queryExecStart;
    private long queryExecEnd;
    private long tries = 0;

    public void recordQueryPrepStart() {
      queryPrepStart = System.nanoTime();
    }

    public void recordQueryPrepEnd() {
      queryPrepEnd = System.nanoTime();
    }

    public void recordQueryExecStart() {
      queryExecStart = System.nanoTime();
    }

    public void recordQueryExecEnd() {
      queryExecEnd = System.nanoTime();
    }

    public void recordAttempt() {
      tries++;
    }

    public QueryStatistics calculate() {
      return new QueryStatistics(queryExecEnd - queryExecStart, queryPrepEnd - queryPrepStart, tries);
    }
  }

}
