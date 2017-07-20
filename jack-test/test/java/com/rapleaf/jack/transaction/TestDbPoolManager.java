package com.rapleaf.jack.transaction;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.collect.Lists;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.exception.NoAvailableConnectionException;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestDbPoolManager extends JackTestCase {
  private static final Logger LOG = LoggerFactory.getLogger(TestDbPoolManager.class);

  private static Callable<IDatabase1> DB_CONSTRUCTOR = () -> new DatabasesImpl().getDatabase1();
  private ExecutorService executorService;
  private DbPoolManager<IDatabase1> dbPoolManager;
  private int maxConnections;
  private int minIdleConnections;
  private long maxWaitTime;
  private long keepAliveTime;
  private boolean metricsTrackingEnabled;

  @Before
  public void prepare() throws Exception {
    this.maxConnections = DbPoolManager.DEFAULT_MAX_TOTAL_CONNECTIONS;
    this.minIdleConnections = DbPoolManager.DEFAULT_MIN_IDLE_CONNECTIONS;
    this.maxWaitTime = DbPoolManager.DEFAULT_MAX_WAIT_TIME;
    this.keepAliveTime = DbPoolManager.DEFAULT_KEEP_ALIVE_TIME;
    this.metricsTrackingEnabled = DbPoolManager.DEFAULT_METRICS_TRACKING_ENABLED;
  }

  @After
  public void cleanup() throws Exception {
    this.dbPoolManager = null;
    this.executorService = null;
  }

  private void initializeDbPoolManager() {
    dbPoolManager = new DbPoolManager<>(DB_CONSTRUCTOR, maxConnections, minIdleConnections, maxWaitTime, keepAliveTime, metricsTrackingEnabled);
  }

  @Test
  public void testMaxTotalConnections() throws Exception {
    maxConnections = 5;
    maxWaitTime = 0L;
    initializeDbPoolManager();
    getAllConnections();
    assertIdleConnections(0);
    assertActiveConnections(maxConnections);

    try {
      dbPoolManager.getConnection();
      fail();
    } catch (NoAvailableConnectionException e) {
      // expected
    }
  }

  @Test
  public void testMinIdleConnections() throws Exception {
    maxConnections = 10;
    minIdleConnections = 2;
    keepAliveTime = 2;
    initializeDbPoolManager();
    getAndReturnAllConnections();

    // wait for eviction
    sleepSeconds(1);

    assertIdleConnections(minIdleConnections);
  }

  @Test(timeout = 10 * 1000L) // 10s
  public void testKeepAliveTime() throws Exception {
    maxConnections = 15;
    minIdleConnections = 5;
    keepAliveTime = Duration.standardSeconds(1).getMillis();
    initializeDbPoolManager();
    getAndReturnAllConnections();

    // all idle connections should be available
    assertIdleConnections(maxConnections);
    assertEvictionTime((int)keepAliveTime / 1000, 2);
  }

  @Test(timeout = 10 * 1000L) // 10s
  public void testMaxWaitTime() throws Exception {
    maxConnections = 1;
    maxWaitTime = Duration.standardSeconds(8).getMillis();
    initializeDbPoolManager();

    executorService = Executors.newFixedThreadPool(2);

    /*
     * 1. first thread gets the only connection, and takes firstThreadProcessTime seconds to return the connection
     */
    int firstThreadProcessTime = 3;
    Future future1 = startFirstThread(firstThreadProcessTime);

    /*
     * 2. second thread starts after secondThreadStartDelay seconds, and gets the connection after waiting for
     *    about firstThreadProcessTime - secondThreadStartDelay seconds
     */
    int secondThreadStartDelay = 1;
    sleepSeconds(secondThreadStartDelay);

    int expectedWaitSeconds = firstThreadProcessTime - secondThreadStartDelay;

    Future future2 = executorService.submit(() -> {
      LOG.info("Second task started");

      // no connection at first
      assertIdleConnections(0);

      // wait for connection
      long startTime = System.currentTimeMillis();
      dbPoolManager.getConnection();

      // check wait time
      int waitSeconds = getSecondsSince(startTime);
      LOG.info("Second task DB waited for connection: {} seconds", waitSeconds);
      assertRoughEqual(waitSeconds, expectedWaitSeconds, 2);

      LOG.info("Second task completed");
    });

    future1.get();
    future2.get();

    executorService.shutdownNow();
  }

  @Test(timeout = 10 * 1000L) // 10s
  public void testNoAvailableConnectionAfterWait() throws Exception {
    maxConnections = 1;
    maxWaitTime = Duration.standardSeconds(1).getMillis();
    initializeDbPoolManager();

    executorService = Executors.newFixedThreadPool(2);

    /*
     * 1. first thread gets the only connection, and takes firstThreadProcessTime seconds to return the connection
     */
    int firstThreadProcessTime = 3;
    Future future1 = startFirstThread(firstThreadProcessTime);

    /*
     * 2. second thread starts after secondThreadStartDelay seconds, and gets the connection after waiting for
     *    about firstThreadProcessTime - secondThreadStartDelay seconds
     */
    int secondThreadStartDelay = 1;
    sleepSeconds(secondThreadStartDelay);

    Future future2 = executorService.submit(() -> {
      LOG.info("Second task started");

      // no connection at first
      assertEquals(0, dbPoolManager.getConnectionPool().getNumIdle());

      // wait for connection
      long startTime = System.currentTimeMillis();
      try {
        dbPoolManager.getConnection();
        fail();
      } catch (NoAvailableConnectionException e) {
        // check exception is thrown after wait time
        int waitSeconds = getSecondsSince(startTime);
        LOG.info("Second task DB waited for connection: {} seconds", waitSeconds);
        assertRoughEqual(waitSeconds, maxWaitTime / 1000, 1);
      }

      LOG.info("Second task completed");
    });

    future1.get();
    future2.get();

    executorService.shutdownNow();
  }

  private void getAllConnections() {
    for (int i = 0; i < maxConnections; ++i) {
      dbPoolManager.getConnection();
    }
  }

  private void getAndReturnAllConnections() {
    List<IDatabase1> connections = Lists.newLinkedList();
    for (int i = 0; i < maxConnections; ++i) {
      connections.add(dbPoolManager.getConnection());
    }
    assertIdleConnections(0);
    assertActiveConnections(maxConnections);

    for (IDatabase1 connection : connections) {
      dbPoolManager.returnConnection(connection);
    }
  }

  private int getSecondsSince(long startTime) {
    return Duration.millis(System.currentTimeMillis() - startTime).toStandardSeconds().getSeconds();
  }

  private void assertIdleConnections(int idleConnections) {
    assertEquals(idleConnections, dbPoolManager.getConnectionPool().getNumIdle());
  }

  private void assertActiveConnections(int activeConnections) {
    assertEquals(activeConnections, dbPoolManager.getConnectionPool().getNumActive());
  }

  private void assertRoughEqual(long value, long expected, long error) {
    assertTrue(value <= (expected + error) && value >= (expected - error));
  }

  private void assertInRange(long value, long expectedMin, long expectedMax) {
    assertTrue(value >= expectedMin && value <= expectedMax);
  }

  private void assertEvictionTime(int expectedSeconds, int error) {
    long startTime = System.currentTimeMillis();
    while (true) {
      LOG.info("Active connections: {}", dbPoolManager.getConnectionPool().getNumIdle());
      if (dbPoolManager.getConnectionPool().getNumIdle() == minIdleConnections) {
        int waitSeconds = getSecondsSince(startTime);
        LOG.info("Idle connections are evicted after {} seconds", waitSeconds);
        // allow two seconds delay
        assertInRange(waitSeconds, expectedSeconds, expectedSeconds + error);
        break;
      } else {
        sleepMillis(500);
      }
    }
  }

  private Future startFirstThread(int threadProcessTime) {
    return executorService.submit(() -> {
      LOG.info("First task started");

      // get connection
      IDatabase1 connections = dbPoolManager.getConnection();
      assertIdleConnections(0);

      // return connection after sleepSeconds
      long startTime = System.currentTimeMillis();
      sleepSeconds(threadProcessTime);
      dbPoolManager.returnConnection(connections);

      // check connection use time
      int returnTime = getSecondsSince(startTime);
      LOG.info("First task returned DB connection in {} seconds", returnTime);
      assertTrue(returnTime >= threadProcessTime);

      LOG.info("First task completed");
    });
  }

}
