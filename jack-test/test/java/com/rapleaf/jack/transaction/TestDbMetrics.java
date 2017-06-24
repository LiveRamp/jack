package com.rapleaf.jack.transaction;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

public class TestDbMetrics extends JackTestCase {
  private TransactorImpl.Builder<IDatabase1> transactorBuilder = new DatabasesImpl().getDatabase1Transactor();
  private ExecutorService executorService;
  private static final Logger LOG = LoggerFactory.getLogger(TestDbMetrics.class);

  @Before
  public void prepare() throws Exception {
    transactorBuilder.get().query(IDb::deleteAll);
  }

  @After
  public void cleanup() throws Exception {
    executorService = null;
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
    executorService = Executors.newFixedThreadPool(5);
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(2).get();

    Future future1 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(100);}));
    Future future2 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(100);}));
    future1.get();
    future2.get();
    sleepMillis(100);
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double maxConnectionsProportion = dbMetrics.getMaxConnectionsProportion();
    transactor.close();
    assert (maxConnectionsProportion > .3);
  }


  @Test
  public void testMaxConnectionWaitingTime() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(1).get();
    executorService = Executors.newFixedThreadPool(5);

    Future future1 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(100);}));
    Future future2 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(50);}));
    future1.get();
    future2.get();
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double maxConnectionsWaitingTime = dbMetrics.getMaxConnectionWaitingTime();
    transactor.close();
    assert (maxConnectionsWaitingTime >= 100);
  }

  @Test
  public void testAverageConnectionWaitingTime() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(1).get();
    executorService = Executors.newFixedThreadPool(5);

    Future future1 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(100);}));
    Future future2 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(100);}));
    future1.get();
    future2.get();
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double averageConnectionWaitTime = dbMetrics.getAverageConnectionWaitTime();
    transactor.close();
    assert (averageConnectionWaitTime >= 50);
  }

  @Test
  public void testAverageConnectionExecutionTime() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(2).get();
    executorService = Executors.newFixedThreadPool(5);

    Future future1 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(100);}));
    Future future2 = executorService.submit(() -> transactor.execute(a -> {sleepMillis(100);}));
    future1.get();
    future2.get();
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double averageConnectionExecutionTime = dbMetrics.getAverageConnectionExecutionTime();
    transactor.close();

    assert (averageConnectionExecutionTime >= 100);
  }

  @Test
  public void testAverageIdleConnections() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(2).setMinIdleConnections(1).setKeepAliveTime(Duration.millis(50)).get();
    transactor.execute(a -> {sleepMillis(100);});
    sleepMillis(100);
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double averageIdleConnectionsMaxValue = dbMetrics.getAverageIdleConnectionsMaxValue();
    transactor.close();
    assert (averageIdleConnectionsMaxValue <= .6);
  }

  @Test
  public void testAverageActiveConnections() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    transactor.execute(db -> {sleepMillis(100);});
    sleepMillis(100);
    DbMetrics dbMetrics = transactor.getDbMetrics();
    double averageActiveConnections = dbMetrics.getAverageActiveConnections();
    transactor.close();
    assert ((averageActiveConnections > .40) && (averageActiveConnections < .60));
  }

}
