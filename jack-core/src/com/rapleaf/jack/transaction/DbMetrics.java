package com.rapleaf.jack.transaction;

public interface DbMetrics {
  long getTotalQueries();

  long getOpenedConnectionsNumber();

  double getMaxConnectionsProportion();

  long getMaxConnectionWaitingTime();

  double getAverageConnectionWaitTime();

  double getAverageConnectionExecutionTime();

  double getAverageIdleConnectionsMaxValue();

  double getAverageIdleConnectionsMinValue();

  double getAverageActiveConnections();

  void pause();

  void resume();

  String getSummary();
}
