package com.rapleaf.jack.transaction;

public class MockDbMetrics implements DbMetrics {
  @Override
  public long getTotalQueries() {
    return 0;
  }

  @Override
  public long getCreatedConnectionsCount() {
    return 0;
  }

  @Override
  public double getMaxConnectionsProportion() {
    return 0;
  }

  @Override
  public long getMaxConnectionWaitingTime() {
    return 0;
  }

  @Override
  public double getAverageConnectionWaitingTime() {
    return 0;
  }

  @Override
  public double getAverageIdleConnections() {
    return 0;
  }

  @Override
  public double getAverageActiveConnections() {
    return 0;
  }

  @Override
  public long getLifeTime() {
    return 0;
  }

  @Override
  public String getSummary() {
    return "Mock db metrics";
  }
}
