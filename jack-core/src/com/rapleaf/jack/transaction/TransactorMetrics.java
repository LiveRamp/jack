package com.rapleaf.jack.transaction;

import java.util.LinkedHashMap;

public interface TransactorMetrics {

  long getMaxExecutionTime();

  StackTraceElement getMaxExecutionTimeQuery();

  LinkedHashMap<StackTraceElement, Long> getLongestQueries();

  String getSummary();

}
