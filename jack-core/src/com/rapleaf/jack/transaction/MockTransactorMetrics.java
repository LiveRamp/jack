package com.rapleaf.jack.transaction;

import java.util.LinkedList;

public class MockTransactorMetrics implements TransactorMetrics {
  @Override
  public LinkedList<TransactorMetricElement> getLongestQueries() {
    return new LinkedList<>();
  }

  @Override
  public double getAverageQueryExecutionTime() {
    return 0;
  }

  @Override
  public String getSummary() {
    return "Mock transactor metrics";
  }
}
