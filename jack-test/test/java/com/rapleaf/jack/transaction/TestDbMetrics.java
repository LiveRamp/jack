package com.rapleaf.jack.transaction;


import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Stopwatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;

import static org.junit.Assert.assertTrue;

public class TestDbMetrics extends JackTestCase {
  private TransactorImpl.Builder<IDatabase1> transactorBuilder = new DatabasesImpl().getDatabase1Transactor();
  private ExecutorService executorService;
  private Stopwatch stopwatch = new Stopwatch();
  private static final Logger LOG = LoggerFactory.getLogger(TestDbMetrics.class);

  @Before
  public void prepare() throws Exception {
    stopwatch.start();
    executorService = Executors.newFixedThreadPool(5);
  }

  @After
  public void cleanup() throws Exception {
    executorService.shutdown();
    stopwatch.reset();
  }

  @Test
  public void testTotalQueries() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMetricsTracking(true).get();
    transactor.execute(db -> {
    });
    DbMetrics dbMetrics = transactor.getDbMetrics();

    assertTrue(dbMetrics.getTotalQueries() == 1);
  }

  @Test
  public void testOpenedConnectionsNumber() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMetricsTracking(true).setMaxTotalConnections(2).get();

    Future future1 = executorService.submit(() -> transactor.execute(a -> {
      sleepMillis(50);
    }));
    Future future2 = executorService.submit(() -> transactor.execute(a -> {
      sleepMillis(70);
    }));
    Future future3 = executorService.submit(() -> transactor.execute(a -> {
      sleepMillis(90);
    }));
    future1.get();
    future2.get();
    future3.get();
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double openedConnectionsNumber = dbMetrics.getCreatedConnectionsCount();
    transactor.close();
    assertTrue(openedConnectionsNumber == 2);
  }

  @Test
  public void testMaxConnectionsProportion() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMetricsTracking(true).setMaxTotalConnections(1).get();
    transactor.execute(a -> {
      sleepMillis(100);
    });
    long timeActive = stopwatch.elapsedMillis();
    sleepMillis(100);
    long totalTime = stopwatch.elapsedMillis();
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double maxConnectionsProportion = dbMetrics.getMaxCapacityProportion();
    transactor.close();
    double expectedMaxConnectionsProportion = (double)timeActive / (double)totalTime;

    assertRoughEqual(maxConnectionsProportion, expectedMaxConnectionsProportion, .1);
  }


  @Test
  public void testMaxConnectionWaitingTime() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMetricsTracking(true).setMaxTotalConnections(1).get();
    transactor.execute(a -> {
    });
    double firstMaxConnectionWaitingTime = transactor.getDbMetrics().getMaxConnectionWaitingTime();
    int connectionCount = 5;
    final Long[] startingTimes = new Long[connectionCount];
    final Long[] schedulingTimes = new Long[connectionCount];
    Future[] futures = new Future[connectionCount];
    for (int i = 0; i < connectionCount; i++) {
      int finalI = i;
      futures[i] = executorService.submit(() ->
      {
        schedulingTimes[finalI] = stopwatch.elapsedMillis();
        transactor.execute(a -> {
          startingTimes[finalI] = stopwatch.elapsedMillis();
          sleepMillis(100);
        });
      });
    }
    long measuredMaxWaitingTime = 0;
    for (int i = 0; i < connectionCount; i++) {
      futures[i].get();
      measuredMaxWaitingTime = Math.max(measuredMaxWaitingTime, startingTimes[i] - schedulingTimes[i]);
    }
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double maxConnectionsWaitingTime = dbMetrics.getMaxConnectionWaitingTime();
    transactor.close();
    double expectedMaxConnectionsWaitingTime = Math.max(measuredMaxWaitingTime, firstMaxConnectionWaitingTime);

    assertRoughEqual(maxConnectionsWaitingTime, expectedMaxConnectionsWaitingTime, 100);
    //The first setAutoCommit method call is slower than later calls. So the first call is treated differently in the test

  }

  @Test
  public void testAverageConnectionWaitingTime() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMetricsTracking(true).setMaxTotalConnections(1).get();
    transactor.execute(a -> {
    });
    double firstAverageConnectionWaitingTime = transactor.getDbMetrics().getAverageConnectionWaitingTime();
    int connectionCount = 5;
    final Long[] startingTimes = new Long[connectionCount];
    final Long[] schedulingTimes = new Long[connectionCount];
    Future[] futures = new Future[connectionCount];
    for (int i = 0; i < connectionCount; i++) {
      int finalI = i;
      futures[i] = executorService.submit(() ->
      {
        schedulingTimes[finalI] = stopwatch.elapsedMillis();
        transactor.execute(a -> {
          startingTimes[finalI] = stopwatch.elapsedMillis();
          sleepMillis(100);
        });
      });
    }
    long startingTimeSum = 0;
    long schedulingTimeSum = 0;
    for (int i = 0; i < connectionCount; i++) {
      futures[i].get();
      startingTimeSum += startingTimes[i];
      schedulingTimeSum += schedulingTimes[i];
    }
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double averageConnectionsWaitingTime = dbMetrics.getAverageConnectionWaitingTime();
    transactor.close();
    double expectedAverageConnectionsWaitingTime = ((startingTimeSum - schedulingTimeSum) + firstAverageConnectionWaitingTime) / (connectionCount + 1);

    assertRoughEqual(averageConnectionsWaitingTime, expectedAverageConnectionsWaitingTime, .2 * expectedAverageConnectionsWaitingTime);
    //The first setAutoCommit method call is slower than later calls. So the first call is treated differently in the test

  }

  @Test
  public void testAverageIdleConnections() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMetricsTracking(true).setMaxTotalConnections(2).setMinIdleConnections(1).setKeepAliveTime(Duration.ofMillis(50)).get();
    transactor.execute(a -> {
      sleepMillis(100);
    });
    long startIdleTime = stopwatch.elapsedMillis();
    sleepMillis(100);
    long idleTime = stopwatch.elapsedMillis() - startIdleTime;
    DbMetrics dbMetrics = transactor.getDbMetrics();
    long lifeTime = dbMetrics.getLifeTime();
    double expectedAverageIdleConnections = (double)idleTime / (double)lifeTime;
    double averageIdleConnections = dbMetrics.getAverageIdleConnections();
    transactor.close();
    assertRoughEqual(expectedAverageIdleConnections, averageIdleConnections, .2 * expectedAverageIdleConnections);
  }

  @Test
  public void testAverageActiveConnections() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMetricsTracking(true).get();
    long startActive = stopwatch.elapsedMillis();
    transactor.execute(db -> {
      sleepMillis(100);
    });
    long activeTime = stopwatch.elapsedMillis() - startActive;
    sleepMillis(100);
    DbMetrics dbMetrics = transactor.getDbMetrics();
    long lifeTime = dbMetrics.getLifeTime();
    double expectedAverageActiveConnections = (double)activeTime / (double)lifeTime;
    double averageActiveConnections = dbMetrics.getAverageActiveConnections();
    transactor.close();

    assertRoughEqual(expectedAverageActiveConnections, averageActiveConnections, .2);
  }

  @Test
  public void testLifeTime() throws Exception {
    long startTime = stopwatch.elapsedMillis();
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMetricsTracking(true).get();
    sleepMillis(100);
    DbMetrics dbMetrics = transactor.getDbMetrics();

    assertRoughEqual(stopwatch.elapsedMillis() - startTime, dbMetrics.getLifeTime(), 20);
  }

  private void assertRoughEqual(double value, double expected, double error) {
    assertTrue(value <= (expected + error) && value >= (expected - error));
  }

}
