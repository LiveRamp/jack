package com.rapleaf.jack.transaction;

import java.util.LinkedHashMap;

public interface TransactorMetrics {

  long getMaxExecutionTime();

  StackTraceElement getMaxExecutionTimeQuery();

  LinkedHashMap<StackTraceElement, Long> getLongestQueries();

  void update(long executionTime, StackTraceElement queryStackTrace);

  String getSummary();

}
