package com.rapleaf.jack.transaction;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactorMetricsImpl implements TransactorMetrics {
  int longestQueriesSize;
  private static Comparator<TransactorMetricElement> queryComparator = new TransactorMetricElementsComparator();
  private static final Logger LOG = LoggerFactory.getLogger(TransactorMetricsImpl.class);

  PriorityQueue<TransactorMetricElement> longestQueries;
  HashMap<StackTraceElement, TransactorMetricElement> longestQueriesMap;
  long totalExecutionTime = 0;
  long queryCount = 0;
  /*
  Algorithm description :

  The goal is to output the K queries with the longest average execution time. (k=longestQueriesSize)
  The algorithm is probabilistic :http://www.cse.ust.hk/~raywong/comp5331/References/EfficientComputationOfFrequentAndTop-kElementsInDataStreams.pdf
  What we know for sure is that if a query Q has an average execution time  greater than the lowest execution time of the
  output queries, then Q is part of the output queries.

  We maintain throughout the algorithm a priority queue with the queries with the largest average execution time, ordered by average execution time.
  Each query is wrapped inside an object, TransactorMetricElement, which keeps track of the query's count and total execution time.

  To help removing and adding elements inside this priority queue, we keep a map to link the queryStackTraces to the TransactorMetricElements

  Let k=size of the list and n the number of executed queries.
  The complexity of this algorithm is O(n*log(k))
   */

  TransactorMetricsImpl(int longestQueriesSize) {
    this.longestQueriesSize = longestQueriesSize;
    this.longestQueriesMap = new HashMap<>();
    this.longestQueries = new PriorityQueue<>(longestQueriesSize, queryComparator);
  }

  synchronized void update(long executionTime, StackTraceElement queryStackTrace) {
    totalExecutionTime += executionTime;
    queryCount += 1;

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
      } else if (longestQueries.isEmpty()) {
        LOG.error("synchronization issue : longestQueries shouldn't be empty if longestQueriesMap.size()>=longestQueriesSize");
      } else if (queryComparator.compare(newQuery, longestQueries.peek()) > 0) {
        TransactorMetricElement removedQuery = longestQueries.poll();
        longestQueriesMap.remove(removedQuery.getQueryTrace());
        longestQueriesMap.put(newQuery.getQueryTrace(), newQuery);
        longestQueries.add(newQuery);
      }
    }
  }

  @Override
  public double getAverageQueryExecutionTime() {
    return (double)totalExecutionTime / (double)queryCount;
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
    String summary = ("\n-----------------------QUERY METRICS-----------------------\n");
    summary += String.format("\n Average Query execution time :  %,.2f ms", getAverageQueryExecutionTime());
    summary += "\n\n------" + longestQueriesSize + " QUERIES WITH LONGEST AVERAGE EXECUTION TIME------\n";
    for (TransactorMetricElement query : longestQueriesList) {
      summary += "\nClass name : " + query.getQueryTrace().getClassName();
      summary += "\nLine number : " + query.getQueryTrace().getLineNumber();
      summary += String.format("\nAverage execution runtime : %,.2f ms", query.getAverageExecutionTime());
      summary += "\nExecution count >= " + query.getCount();
    }
    return summary;
  }
}
