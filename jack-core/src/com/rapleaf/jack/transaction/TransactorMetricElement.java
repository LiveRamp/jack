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

  @Override
  public int hashCode() {
    int hash = queryTrace.hashCode();
    hash += 137 * totalExecutionTime;
    hash += 197 * count;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(this.getClass() == obj.getClass())) {
      return false;
    } else {
      TransactorMetricElement that = (TransactorMetricElement)obj;
      return ((this.queryTrace.equals(that.queryTrace)) && (this.totalExecutionTime == that.totalExecutionTime) && (this.count == that.count));
    }
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

  public void addExecution(long executionTime) {
    count += 1;
    totalExecutionTime += executionTime;
  }
}

class TransactorMetricElementsComparator implements Comparator<TransactorMetricElement> {

  @Override
  public int compare(TransactorMetricElement a, TransactorMetricElement b) {
    if (a.getAverageExecutionTime() >= b.getAverageExecutionTime()) {
      return -1;
    } else {
      return 1;
    }
  }

}
