package com.rapleaf.jack.transaction;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.base.Stopwatch;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
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
    transactorBuilder.get().query(IDb::deleteAll);
    stopwatch.start();
  }

  @After
  public void cleanup() throws Exception {
    executorService = null;
    stopwatch.reset();
  }

  @Test
  public void testTotalQueries() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    transactor.execute(db -> {});
    DbMetrics dbMetrics = transactor.getDbMetrics();
    assert (dbMetrics.getTotalQueries() == 1);
  }

  @Test
  public void testOpenedConnectionsNumber() throws Exception {
    executorService = Executors.newFixedThreadPool(5);
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(2).get();

    Future future1 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(50);}));
    Future future2 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(50);}));
    Future future3 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(50);}));
    future1.get();
    future2.get();
    future3.get();
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double openedConnectionsNumber = dbMetrics.getOpenedConnectionsNumber();
    transactor.close();
    assert (openedConnectionsNumber == 2);
  }

  @Test
  public void testMaxConnectionsProportion() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(1).get();
    transactor.execute(a -> {sleepMillis(100);});
    long timeActive = stopwatch.elapsedMillis();
    sleepMillis(100);
    long totalTime = stopwatch.elapsedMillis();
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double maxConnectionsProportion = dbMetrics.getMaxConnectionsProportion();
    transactor.close();
    double expectedMaxConnectionsProportion = (double)timeActive / (double)totalTime;
    assertRoughEqual(maxConnectionsProportion, expectedMaxConnectionsProportion, .1);
  }


  @Test
  public void testMaxConnectionWaitingTime() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(1).get();
    executorService = Executors.newFixedThreadPool(2);

    Future<Long> future1 = executorService.submit(() -> transactor.query(a -> {
      sleepMillis(100);
      return stopwatch.elapsedMillis();
    }));
    final Long[] startingTime2 = new Long[1];
    Future future2 = executorService.submit(
        () -> {
          startingTime2[0] = stopwatch.elapsedMillis();
          transactor.execute(a -> {sleepMillis(100);});
        });
    long finishingTime1 = future1.get();
    future2.get();
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double maxConnectionsWaitingTime = dbMetrics.getMaxConnectionWaitingTime();
    transactor.close();
    double expectedMaxConnectionsWaitingTime = (finishingTime1 - startingTime2[0]);
    expectedMaxConnectionsWaitingTime = (expectedMaxConnectionsWaitingTime > 0) ? expectedMaxConnectionsWaitingTime : 0;
    assertRoughEqual(maxConnectionsWaitingTime, expectedMaxConnectionsWaitingTime, 20);
  }

  @Test
  public void testAverageConnectionWaitingTime() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(1).get();
    executorService = Executors.newFixedThreadPool(2);

    Future<Long> future1 = executorService.submit(() -> transactor.query(a -> {
      sleepMillis(100);
      return stopwatch.elapsedMillis();
    }));
    final Long[] startingTime2 = new Long[1];
    Future future2 = executorService.submit(
        () -> {
          startingTime2[0] = stopwatch.elapsedMillis();
          transactor.execute(a -> {sleepMillis(100);});
        });
    long finishingTime1 = future1.get();
    future2.get();
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double averageConnectionsWaitingTime = dbMetrics.getAverageConnectionWaitingTime();
    transactor.close();
    double expectedAverageConnectionsWaitingTime = (finishingTime1 - startingTime2[0]) / 2;
    expectedAverageConnectionsWaitingTime = (expectedAverageConnectionsWaitingTime > 0) ? expectedAverageConnectionsWaitingTime : 0;
    assertRoughEqual(averageConnectionsWaitingTime, expectedAverageConnectionsWaitingTime, 15);
  }

  @Test
  public void testAverageConnectionExecutionTime() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(2).get();
    executorService = Executors.newFixedThreadPool(5);
    Future<Long> future1 = executorService.submit(() -> {
      long start = stopwatch.elapsedMillis();
      transactor.execute(a -> {sleepMillis(100);});
      return stopwatch.elapsedMillis() - start;
    });
    Future<Long> future2 = executorService.submit(() -> {
      long start = stopwatch.elapsedMillis();
      transactor.execute(a -> {sleepMillis(100);});
      return stopwatch.elapsedMillis() - start;
    });
    double expectedAverageConnectionExecutionTime = ((double)future1.get() + (double)future2.get()) / 2;
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double averageConnectionExecutionTime = dbMetrics.getAverageConnectionExecutionTime();
    transactor.close();

    assertRoughEqual(averageConnectionExecutionTime, expectedAverageConnectionExecutionTime, 20);
  }

  @Test
  public void testAverageIdleConnections() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(2).setMinIdleConnections(1).setKeepAliveTime(Duration.millis(50)).get();
    transactor.execute(a -> {sleepMillis(100);});
    long startIdleTime = stopwatch.elapsedMillis();
    sleepMillis(100);
    long idleTime = stopwatch.elapsedMillis() - startIdleTime;
    DbMetrics dbMetrics = transactor.getDbMetrics();
    long lifeTime = dbMetrics.getLifeTime();
    double expectedAverageIdleConnections = (double)idleTime / (double)lifeTime;
    double averageIdleConnectionsMaxValue = dbMetrics.getAverageIdleConnectionsMaxValue();
    double averageIdleConnectionsMinValue = dbMetrics.getAverageIdleConnectionsMinValue();
    transactor.close();

    assert ((expectedAverageIdleConnections - .1 <= averageIdleConnectionsMaxValue) && (expectedAverageIdleConnections + .1 >= averageIdleConnectionsMinValue));
  }

  @Test
  public void testAverageActiveConnections() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    long startActive = stopwatch.elapsedMillis();
    transactor.execute(db -> {sleepMillis(100);});
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
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    sleepMillis(100);
    DbMetrics dbMetrics = transactor.getDbMetrics();
    assertRoughEqual(stopwatch.elapsedMillis() - startTime, dbMetrics.getLifeTime(), 20);
  }

  private void assertRoughEqual(double value, double expected, double error) {
    assertTrue(value <= (expected + error) && value >= (expected - error));
  }

}


