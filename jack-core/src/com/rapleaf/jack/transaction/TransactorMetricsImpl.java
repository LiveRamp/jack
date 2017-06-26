package com.rapleaf.jack.transaction;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class TransactorMetricsImpl implements TransactorMetrics {
  int longestQueriesSize;

  PriorityQueue<TransactorMetricElement> longestQueries;
  HashMap<StackTraceElement, TransactorMetricElement> longestQueriesMap;
  Comparator<TransactorMetricElement> queryComparator;

  //Contains a map with the *longestQueriesListSize* queries that took the most time to execute and their execution time

  TransactorMetricsImpl(int longestQueriesSize) {
    this.longestQueriesSize = longestQueriesSize;
    this.longestQueriesMap = new HashMap<>();
    this.queryComparator = new TransactorMetricElementsComparator().reversed();
    this.longestQueries = new PriorityQueue<>(longestQueriesSize, queryComparator);
  }

  synchronized void update(long executionTime, StackTraceElement queryStackTrace) {
    if (longestQueriesMap.containsKey(queryStackTrace)) {
      TransactorMetricElement query = longestQueriesMap.get(queryStackTrace);
      longestQueries.remove(query);
      query.addExecution(executionTime);
      longestQueries.add(query);
    } else {
      TransactorMetricElement newQuery = new TransactorMetricElement(queryStackTrace, executionTime);
      if (longestQueriesMap.size() < longestQueriesSize) {
        longestQueriesMap.put(queryStackTrace, newQuery);
        longestQueries.add(newQuery);
      } else if (queryComparator.compare(newQuery, longestQueries.peek()) > 0) {
        TransactorMetricElement removedQuery = longestQueries.poll();
        longestQueriesMap.remove(removedQuery.getQueryTrace());
        longestQueriesMap.put(newQuery.getQueryTrace(), newQuery);
        longestQueries.add(newQuery);
      }
    }
  }

  @Override
  public synchronized LinkedList<TransactorMetricElement> getLongestQueries() {
    LinkedList<TransactorMetricElement> longestQueriesList = new LinkedList<>();
    while (longestQueries.size() > 0) {
      longestQueriesList.add(longestQueries.poll());
    }
    longestQueries.addAll(longestQueriesList);
    return longestQueriesList;
  }

  @Override
  public String getSummary() {
    LinkedList<TransactorMetricElement> longestQueriesList = getLongestQueries();
    String log = "";
    log += "\n--------------" + longestQueriesSize + " QUERIES WITH LONGEST AVERAGE EXECUTION TIME-----------------\n";
    for (TransactorMetricElement query : longestQueriesList) {
      log += "\nClass name : " + query.getQueryTrace().getClassName();
      log += "\nLine number : " + query.getQueryTrace().getLineNumber();
      log += String.format("\nAverage execution runtime : %,.2f", query.getAverageExecutionTime());
      log += "\nExecution count >= " + query.getCount();
    }
    return log;
  }
}
