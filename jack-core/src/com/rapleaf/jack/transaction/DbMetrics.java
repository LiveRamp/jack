package com.rapleaf.jack.transaction;

public interface DbMetrics {
  long getTotalQueries();

  long getCreatedConnectionsCount();

  double getMaxCapacityProportion();

  long getMaxConnectionWaitingTime();

  double getAverageConnectionWaitingTime();

  double getAverageIdleConnections();

  double getAverageActiveConnections();

  long getLifeTime();

  String getSummary();
}
