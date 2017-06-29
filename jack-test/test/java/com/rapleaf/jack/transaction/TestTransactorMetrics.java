package com.rapleaf.jack.transaction;

import com.google.common.base.Stopwatch;
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

public class TestTransactorMetrics extends JackTestCase {

  private TransactorImpl.Builder<IDatabase1> transactorBuilder = new DatabasesImpl().getDatabase1Transactor();
  private static final Logger LOG = LoggerFactory.getLogger(TestTransactorMetrics.class);
  private Stopwatch stopwatch = new Stopwatch();

  @Before
  public void prepare() throws Exception {
    transactorBuilder.get().query(IDb::deleteAll);
    stopwatch.start();
  }

  @After
  public void cleanup() throws Exception {
    stopwatch.reset();
  }

  @Test
  public void testQueryOrder() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    transactor.execute(db -> {sleepMillis(200);});
    transactor.execute(db -> {sleepMillis(100);});
    transactor.execute(db -> {});

    TransactorMetrics queryMetrics = transactor.getQueryMetrics();
    int lastLine = 0;
    transactor.close();
    for (TransactorMetricElement query : queryMetrics.getLongestQueries()) {
      assertTrue(query.getQueryTrace().getLineNumber() > lastLine);
      lastLine = query.getQueryTrace().getLineNumber();
    }
    transactor.close();
  }

  @Test
  public void testMaxAverageExecutionTime() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    Long executionTime = transactor.query(db -> {
      long startTime = System.currentTimeMillis();
      Thread.sleep(200);
      return System.currentTimeMillis() - startTime;
    });
    TransactorMetrics queryMetrics = transactor.getQueryMetrics();
    double maxExecutionTime = queryMetrics.getLongestQueries().getFirst().getAverageExecutionTime();
    transactor.close();

    assertRoughEqual(executionTime, maxExecutionTime, 20);
  }

  @Test
  public void testQueryOverhead() throws Exception {
    TransactorImpl<IDatabase1> transactor1 = transactorBuilder.setMetricsTracking(false).get();
    long startTime1 = stopwatch.elapsedMillis();
    for (int i = 0; i < 10; i++) {
      transactor1.execute(db -> {
        Thread.sleep(20);
      });
    }
    long finishTime1 = stopwatch.elapsedMillis() - startTime1;
    LOG.info("execution time without query tracking: {} ms", finishTime1);
    transactor1.close();

    TransactorImpl<IDatabase1> transactor2 = transactorBuilder.setMetricsTracking(true).get();
    long startTime2 = stopwatch.elapsedMillis();
    for (int i = 0; i < 10; i++) {
      transactor2.execute(db -> {
        Thread.sleep(20);
      });
    }
    long finishTime2 = stopwatch.elapsedMillis() - startTime2;
    LOG.info("execution time with query tracking: {} ms", finishTime2);
    transactor2.close();
    assertRoughEqual(finishTime1, finishTime2, .2 * finishTime1);
  }

  private void assertRoughEqual(double value, double expected, double error) {
    assertTrue(value <= (expected + error) && value >= (expected - error));
  }
}

