package com.rapleaf.jack.transaction;


import java.util.Comparator;

public class TransactorMetricElement {

  private StackTraceElement queryTrace;
  private long totalExecutionTime;
  private int count;

  public TransactorMetricElement(StackTraceElement queryTrace, long totalExecutionTime) {
    this.queryTrace = queryTrace;
    this.totalExecutionTime = totalExecutionTime;
    this.count = 1;
  }

  public double getAverageExecutionTime() {
    return (double)totalExecutionTime / (double)count;
  }

  public int getCount() {
    return count;
  }

  public StackTraceElement getQueryTrace() {
    return queryTrace;
  }

  public synchronized void addExecution(long executionTime) {
    count += 1;
    totalExecutionTime += executionTime;
  }
}

class TransactorMetricElementsComparator implements Comparator<TransactorMetricElement> {

  public TransactorMetricElementsComparator() {
  }

  @Override
  public int compare(TransactorMetricElement a, TransactorMetricElement b) {
    if (a.getAverageExecutionTime() >= b.getAverageExecutionTime()) {
      return 1;
    } else {
      return -1;
    }
  }

}
