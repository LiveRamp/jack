package com.rapleaf.jack.transaction;

public interface DbMetrics {
  long getTotalQueries();

  long getOpenedConnectionsNumber();

  double getMaxConnectionsProportion();

  long getMaxConnectionWaitingTime();

  double getAverageConnectionWaitTime();

  double getAverageConnectionExecutionTime();

  double getMinConnectionsProportion();

  double getAverageIdleConnections();

  double getAverageActiveConnections();

  String getSummary();
}
