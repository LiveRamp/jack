package com.rapleaf.jack.transaction;

public interface DbMetrics {
  long getTotalQueries();

  long getCreatedConnectionsCount();

  double getMaxConnectionsProportion();

  long getMaxConnectionWaitingTime();

  double getAverageConnectionWaitingTime();

  double getAverageIdleConnections();

  double getAverageActiveConnections();

  long getLifeTime();

  String getSummary();
}
