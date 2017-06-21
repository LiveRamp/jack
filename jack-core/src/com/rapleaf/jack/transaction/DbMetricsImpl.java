package com.rapleaf.jack.transaction;

import com.google.common.base.Stopwatch;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbMetricsImpl implements DbMetrics {

  private Stopwatch lifeTimeStopwatch;
  private long lastUpdateTime;
  private int currentConnections;

  //Raw metrics

  private long totalQueries;
  private long maxActiveConnectionsTime;
  private long minIdleConnectionsTime;
  long maxConnectionWaitTime;
  private long totalConnectionWaitTime;
  private long totalIdleTime;
  private long totalActiveTime;
  private long openedConnections;

  //Transactor parameters

  private int maxTotalConnections;
  private int minIdleConnections;
  private long maxWaitMillis;
  private long keepAliveMillis;

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

  void update(boolean isOpenConnection, final GenericObjectPool connectionPool) {
    try {
      long updateToNowTime = lifeTimeStopwatch.elapsedMillis() - lastUpdateTime;
      lastUpdateTime = lifeTimeStopwatch.elapsedMillis();

      int numActive = connectionPool.getNumActive();
      int numIdle = connectionPool.getNumIdle();
      int numWaiters = connectionPool.getNumWaiters();

      maxConnectionWaitTime = connectionPool.getMaxBorrowWaitTimeMillis();
      totalActiveTime += numActive * updateToNowTime;
      totalIdleTime += numIdle * updateToNowTime;
      totalConnectionWaitTime += numWaiters * updateToNowTime;

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

      if ((numIdle == minIdleConnections) && (numActive == 0)) {
        minIdleConnectionsTime += updateToNowTime;
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
    return (double)totalConnectionWaitTime / (double)totalQueries;
  }

  @Override
  public double getAverageConnectionExecutionTime() {
    return ((double)totalActiveTime / (double)totalQueries);
  }

  @Override
  public double getMinConnectionsProportion() {
    return (double)minIdleConnectionsTime / (double)lifeTimeStopwatch.elapsedMillis();
  }

  @Override
  public long getOpenedConnectionsNumber() {
    return openedConnections;
  }

  @Override
  public double getAverageIdleConnections() {
    return (double)totalIdleTime / (double)lifeTimeStopwatch.elapsedMillis();
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
    summary += ("\nAverage number of Idle connections : " + String.format("%,.2f ms", getAverageIdleConnections()));
    summary += ("\nAverage number of Active connections : " + String.format("%,.2f ms", getAverageActiveConnections()));
    summary += ("\nTotal number of queries/executions : " + getTotalQueries());
    summary += ("\nConnections opened : " + String.valueOf(getOpenedConnectionsNumber()));
    summary += ("\nMax capacity time (%) : " + String.format("%,.2f ms", getMaxConnectionsProportion()));
    summary += ("\nAll idle time (%) : " + String.format("%,.2f ms", getMinConnectionsProportion()));

    summary += ("\nAverage connection execution time : " + String.format("%,.2f ms",
        getAverageConnectionExecutionTime()) + " ms");
    summary += ("\nAverage connection waiting time : " + String.format("%,.2f ms", getAverageConnectionWaitTime()) +
        " ms");
    summary += ("\nMaximum connection waiting time : " + getMaxConnectionWaitingTime() + " ms");

    summary += ("\nTransactor lifetime : " + String.valueOf(lifeTimeStopwatch.elapsedMillis()) + "ms");

    summary += ("\n\n--------------------TRANSACTOR PARAMETERS--------------------");
    summary += ("\nMin idle connections : " + minIdleConnections);
    summary += ("\nMax total connections : " + maxTotalConnections);
    summary += ("\nKeepAliveTime : " + keepAliveMillis + " ms");
    summary += ("\nMaxWaitMillis : " + maxWaitMillis + " ms\n");
    return summary;
  }
}
