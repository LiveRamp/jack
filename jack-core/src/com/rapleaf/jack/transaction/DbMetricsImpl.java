package com.rapleaf.jack.transaction;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbMetricsImpl implements DbMetrics {

  private long startTime;
  private long lastUpdateTime = 0;

  //Raw metrics

  private long totalQueries;
  private long maxActiveConnectionsTime;
  private long maxConnectionWaitingTime;
  private long totalConnectionWaitingTime;
  private long totalIdleTime;
  private long totalActiveTime;
  private long createdConnections;

  //Transactor parameters

  private final int maxTotalConnections;
  private final int minIdleConnections;
  private final long maxWaitMillis;
  private final long keepAliveMillis;

  private static final Logger LOG = LoggerFactory.getLogger(DbMetricsImpl.class);

  public DbMetricsImpl(int maxTotalConnections, int minIdleConnections, long maxWaitMillis, long keepAliveMillis) {
    this.startTime = System.currentTimeMillis();
    this.maxTotalConnections = maxTotalConnections;
    this.minIdleConnections = minIdleConnections;
    this.maxWaitMillis = maxWaitMillis;
    this.keepAliveMillis = keepAliveMillis;
  }


  synchronized void update(boolean isOpenConnection, final GenericObjectPool connectionPool) {

    try {
      long updateToNowTime = getLifeTime() - lastUpdateTime;
      lastUpdateTime = getLifeTime();

      int numActive = connectionPool.getNumActive();
      int numIdle = connectionPool.getNumIdle();
      int numWaiters = connectionPool.getNumWaiters();

      maxConnectionWaitingTime = connectionPool.getMaxBorrowWaitTimeMillis();
      totalActiveTime += numActive * updateToNowTime;
      totalConnectionWaitingTime += numWaiters * updateToNowTime;
      totalIdleTime += numIdle * updateToNowTime;
      if (isOpenConnection) {
        totalQueries += 1;
      }
      int newConnections = numActive + numIdle;
      createdConnections = connectionPool.getCreatedCount();

      if (numActive == maxTotalConnections) {
        maxActiveConnectionsTime += updateToNowTime;
      }

    } catch (Exception e) {
      LOG.error("failed to update statistics", e);
    }
  }

  @Override
  public long getTotalQueries() {
    return totalQueries;
  }

  @Override
  public double getMaxCapacityProportion() {
    return (double)maxActiveConnectionsTime / (double)getLifeTime();
  }

  @Override
  public double getAverageConnectionWaitingTime() {
    if (totalQueries > 0) {
      return (double)totalConnectionWaitingTime / (double)totalQueries;
    } else {
      return -1;
    }
  }

  @Override
  public long getCreatedConnectionsCount() {
    return createdConnections;
  }

  @Override
  public double getAverageIdleConnections() {
    return (double)totalIdleTime / (double)getLifeTime();
  }

  @Override
  public double getAverageActiveConnections() {
    return (double)totalActiveTime / (double)getLifeTime();
  }

  @Override
  public long getMaxConnectionWaitingTime() {
    return maxConnectionWaitingTime;
  }

  public long getLifeTime() {
    return System.currentTimeMillis() - startTime;
  }

  @Override
  public String getSummary() {
    String summary = "";
    summary += ("\n--------------TRANSACTOR CONNECTION METRICS--------------\n");

    summary += String.format("\nAverage number of Idle connections : %,.2f", getAverageIdleConnections());
    summary += String.format("\nAverage number of Active connections : %,.2f", getAverageActiveConnections());
    summary += ("\nTotal number of queries/executions : " + getTotalQueries());
    summary += ("\nConnections created : " + getCreatedConnectionsCount());
    summary += String.format("\n Max capacity time (%%) : %,.2f %%", 100 * getMaxCapacityProportion());
    summary += String.format("\nAverage connection waiting time : %,.2f ms", getAverageConnectionWaitingTime());
    summary += String.format("\nMaximum connection waiting time : %d ms", getMaxConnectionWaitingTime());
    summary += ("\nTransactor lifetime : " + getLifeTime() + " ms");

    summary += ("\n\n----------------TRANSACTOR PARAMETERS----------------");
    summary += ("\nMin idle connections : " + minIdleConnections);
    summary += ("\nMax total connections : " + maxTotalConnections);
    summary += ("\nKeepAliveTime : " + keepAliveMillis + " ms");
    summary += ("\nMaxWaitMillis : " + maxWaitMillis + " ms\n");
    return summary;
  }
}
