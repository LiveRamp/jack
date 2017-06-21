package com.rapleaf.jack.transaction;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactorMetricsImpl implements TransactorMetrics {
  int longestQueriesListSize;


  LinkedList<Long> longestTimes;

  HashMap<StackTraceElement, Long> longestQueries;


  //Contains a map with the *longestQueriesListSize* queries that took the most time to execute and their execution time


  TransactorMetricsImpl(int longestQueriesSize) {
    this.longestQueriesListSize = longestQueriesSize;
    this.longestQueries = new HashMap<>();
    this.longestTimes = new LinkedList<>();
  }


  void update(long executionTime, StackTraceElement queryStackTrace) {
    if (longestQueries.containsKey(queryStackTrace)) {
      if (longestQueries.get(queryStackTrace) < executionTime) {
        longestTimes.removeFirstOccurrence(longestQueries.get(queryStackTrace));
        longestTimes.add(executionTime);
        longestQueries.put(queryStackTrace, executionTime);
        Collections.sort(longestTimes);
      }
    } else {
      if (longestTimes.size() < longestQueriesListSize) {
        longestTimes.add(executionTime);
        longestQueries.put(queryStackTrace, executionTime);
      } else if (executionTime > longestTimes.get(0)) {
        for (StackTraceElement k : longestQueries.keySet()) {
          if (longestQueries.get(k) == longestTimes.get(0)) {
            longestQueries.remove(k);
            longestTimes.remove(0);
            longestTimes.add(executionTime);
            longestQueries.put(queryStackTrace, executionTime);
            Collections.sort(longestTimes);
            break;
          }
        }
      }
    }
  }


  @Override
  public long getMaxExecutionTime() {
    return longestTimes.getLast();
  }

  @Override
  public LinkedHashMap<StackTraceElement, Long> getLongestQueries() {
    return longestQueries.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new
        ));
  }

  @Override
  public StackTraceElement getMaxExecutionTimeQuery() {
    for (StackTraceElement k : longestQueries.keySet()) {
      if (longestQueries.get(k) == getMaxExecutionTime()) {
        return k;
      }
    }
    return null;
  }


  @Override
  public String getSummary() {
    LinkedHashMap<StackTraceElement, Long> sortedMap = getLongestQueries();
    String log = "";
    log += "\n--------------" + longestQueriesListSize + " QUERIES WITH LONGEST EXECUTION TIME-----------------\n";
    for (Map.Entry<StackTraceElement, Long> query : sortedMap.entrySet()) {
      log += "\nClass name : " + query.getKey().getClassName();
      log += "\nLine number : " + query.getKey().getLineNumber();
      log += "\nExecution runtime : " + query.getValue() + "\n";
    }
    return log;

  }
}
