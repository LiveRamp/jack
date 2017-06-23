package com.rapleaf.jack.transaction;

import com.google.common.base.Stopwatch;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbMetricsImpl implements DbMetrics {

  private final Stopwatch lifeTimeStopwatch;
  private long lastUpdateTime = 0;
  private int currentConnections;

  //Raw metrics

  private long totalQueries;
  private long maxActiveConnectionsTime;
  private long maxConnectionWaitTime;
  private long totalConnectionWaitTime;
  private long totalIdleTimeMaxValue;
  private long totalIdleTimeMinValue;
  private long totalActiveTime;
  private long openedConnections;
  
  //Transactor parameters

  private final int maxTotalConnections;
  private final int minIdleConnections;
  private final long maxWaitMillis;
  private final long keepAliveMillis;

  private static final Logger LOG = LoggerFactory.getLogger(DbMetricsImpl.class);

  public DbMetricsImpl(int maxTotalConnections, int minIdleConnections, long maxWaitMillis, long
      keepAliveMillis) {
    this.lifeTimeStopwatch = new Stopwatch();
    this.lifeTimeStopwatch.start();
    this.maxTotalConnections = maxTotalConnections;
    this.minIdleConnections = minIdleConnections;
    this.maxWaitMillis = maxWaitMillis;
    this.keepAliveMillis = keepAliveMillis;
  }

  synchronized void update(boolean isOpenConnection, final GenericObjectPool connectionPool) {

    try {
      long updateToNowTime = lifeTimeStopwatch.elapsedMillis() - lastUpdateTime;
      lastUpdateTime = lifeTimeStopwatch.elapsedMillis();

      int numActive = connectionPool.getNumActive();
      int numIdle = connectionPool.getNumIdle();
      int numWaiters = connectionPool.getNumWaiters();

      maxConnectionWaitTime = connectionPool.getMaxBorrowWaitTimeMillis();
      totalActiveTime += numActive * updateToNowTime;
      totalConnectionWaitTime += numWaiters * updateToNowTime;
      totalIdleTimeMinValue += numIdle * updateToNowTime;
      if (currentConnections > numActive) {
        totalIdleTimeMaxValue += (currentConnections - numActive) * updateToNowTime;
      }
      if (isOpenConnection) {
        totalQueries += 1;
      }
      int newConnections = numActive + numIdle;
      if (newConnections > currentConnections) {
        openedConnections += newConnections - currentConnections;
      }
      currentConnections = newConnections;


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
  public double getMaxConnectionsProportion() {
    return (double)maxActiveConnectionsTime / (double)lifeTimeStopwatch.elapsedMillis();
  }

  @Override
  public double getAverageConnectionWaitTime() {
    if (totalQueries > 0) {
      return (double)totalConnectionWaitTime / (double)totalQueries;
    } else {
      return -1;
    }
  }

  @Override
  public double getAverageConnectionExecutionTime() {
    if (totalQueries > 0) {
      return ((double)totalActiveTime / (double)totalQueries);
    } else {
      return -1;
    }
  }


  @Override
  public long getOpenedConnectionsNumber() {
    return openedConnections;
  }

  @Override
  public double getAverageIdleConnectionsMaxValue() {
    return (double)totalIdleTimeMaxValue / (double)lifeTimeStopwatch.elapsedMillis();
  }

  @Override
  public double getAverageIdleConnectionsMinValue() {
    return (double)totalIdleTimeMinValue / (double)lifeTimeStopwatch.elapsedMillis();
  }

  @Override
  public double getAverageActiveConnections() {
    return (double)totalActiveTime / (double)lifeTimeStopwatch.elapsedMillis();
  }

  @Override
  public long getMaxConnectionWaitingTime() {
    return maxConnectionWaitTime;
  }

  @Override
  public String getSummary() {
    String summary = "";
    summary += ("\n-----------------------TRANSACTOR METRICS-----------------------\n");

    summary += String.format("\nAverage number of Idle connections is between %,.2f and %,.2f", getAverageIdleConnectionsMinValue(), getAverageIdleConnectionsMaxValue());
    summary += String.format("\nAverage number of Active connections : %,.2f", getAverageActiveConnections());
    summary += ("\nTotal number of queries/executions : " + getTotalQueries());
    summary += ("\nConnections opened : " + getOpenedConnectionsNumber());
    summary += String.format("\n Max capacity time (%%)" + " : %,.2f ", getMaxConnectionsProportion());

    summary += String.format("\nAverage connection execution time : %,.2f ms", getAverageConnectionExecutionTime());
    summary += String.format("\nAverage connection waiting time : %,.2f ms", getAverageConnectionWaitTime());
    summary += String.format("\nMaximum connection waiting time : %1$d    ms", getMaxConnectionWaitingTime());
    summary += ("\nTransactor lifetime : " + lifeTimeStopwatch.elapsedMillis() + "ms");

    summary += ("\n\n--------------------TRANSACTOR PARAMETERS--------------------");
    summary += ("\nMin idle connections : " + minIdleConnections);
    summary += ("\nMax total connections : " + maxTotalConnections);
    summary += ("\nKeepAliveTime : " + keepAliveMillis + " ms");
    summary += ("\nMaxWaitMillis : " + maxWaitMillis + " ms\n");
    return summary;
  }
}
