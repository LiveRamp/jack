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

  @Before
  public void prepare() throws Exception {
    this.maxConnections = DbPoolManager.DEFAULT_MAX_TOTAL_CONNECTIONS;
    this.minIdleConnections = DbPoolManager.DEFAULT_MIN_IDLE_CONNECTIONS;
    this.maxWaitTime = DbPoolManager.DEFAULT_MAX_WAIT_TIME;
    this.keepAliveTime = DbPoolManager.DEFAULT_KEEP_ALIVE_TIME;
  }

  @After
  public void cleanup() throws Exception {
    this.dbPoolManager = null;
    this.executorService = null;
  }

  private void initializeDbPoolManager() {
    dbPoolManager = new DbPoolManager<>(DB_CONSTRUCTOR, maxConnections, minIdleConnections, maxWaitTime, keepAliveTime);
  }

  @Test
  public void testMaxTotalConnections() throws Exception {
    maxConnections = 5;
    maxWaitTime = 0L;
    initializeDbPoolManager();

    for (int i = 0; i < maxConnections; ++i) {
      dbPoolManager.getConnection();
    }
    assertEquals(0, dbPoolManager.getConnectionPool().getNumIdle());
    assertEquals(maxConnections, dbPoolManager.getConnectionPool().getNumActive());
  }

  @Test(expected = NoAvailableConnectionException.class)
  public void testImmediateNoAvailableConnectionException() throws Exception {
    maxConnections = 5;
    maxWaitTime = 0L;
    initializeDbPoolManager();

    for (int i = 0; i < maxConnections; ++i) {
      dbPoolManager.getConnection();
    }
    assertEquals(0, dbPoolManager.getConnectionPool().getNumIdle());
    assertEquals(maxConnections, dbPoolManager.getConnectionPool().getNumActive());
    dbPoolManager.getConnection();
  }

  @Test
  public void testMinIdleConnections() throws Exception {
    maxConnections = 10;
    minIdleConnections = 2;
    keepAliveTime = 2;
    initializeDbPoolManager();

    // create and return maxConnections
    List<IDatabase1> connections = Lists.newLinkedList();
    for (int i = 0; i < maxConnections; ++i) {
      connections.add(dbPoolManager.getConnection());
    }
    assertEquals(0, dbPoolManager.getConnectionPool().getNumIdle());
    for (IDatabase1 connection : connections) {
      dbPoolManager.returnConnection(connection);
    }

    // wait for eviction
    Thread.sleep(keepAliveTime * 5);

    // at least minIdleConnections connections in the pool
    int idleConnections = dbPoolManager.getConnectionPool().getNumIdle();
    LOG.info("Idle connections: {}", idleConnections);
    assertTrue(idleConnections >= minIdleConnections && idleConnections < maxConnections);
  }

  @Test(timeout = 10 * 1000L) // 10s
  public void testMaxWaitTime() throws Exception {
    maxConnections = 1;
    maxWaitTime = Duration.standardSeconds(8).getMillis();
    initializeDbPoolManager();

    executorService = Executors.newFixedThreadPool(2);

    // get and return the connection after three seconds
    int returnAfterSeconds = 4;
    Future future1 = executorService.submit(() -> {
      LOG.info("First task started");

      // get connection
      IDatabase1 connections = dbPoolManager.getConnection();
      assertEquals(0, dbPoolManager.getConnectionPool().getNumIdle());

      // return connection after sleep
      long startTime = System.currentTimeMillis();
      sleep(returnAfterSeconds);
      dbPoolManager.returnConnection(connections);
      long endTime = System.currentTimeMillis();

      // check connection use time
      int returnTime = Duration.millis(endTime - startTime).toStandardSeconds().getSeconds();
      LOG.info("First task returned DB connection in {} seconds", returnTime);
      assertTrue(returnTime >= returnAfterSeconds);

      LOG.info("First task completed");
    });

    int sleepSeconds = 1;
    int expectedWaitSeconds = returnAfterSeconds - sleepSeconds;
    sleep(sleepSeconds);

    Future future2 = executorService.submit(() -> {
      LOG.info("Second task started");

      // no connection at first
      assertEquals(0, dbPoolManager.getConnectionPool().getNumIdle());

      // wait for connection
      long startTime = System.currentTimeMillis();
      dbPoolManager.getConnection();
      long endTime = System.currentTimeMillis();

      // check wait time
      int waitSeconds = Duration.millis(endTime - startTime).toStandardSeconds().getSeconds();
      LOG.info("Second task DB waited for connection: {} seconds", waitSeconds);
      assertTrue(waitSeconds >= (expectedWaitSeconds - 2) && waitSeconds <= (expectedWaitSeconds + 2));

      LOG.info("Second task completed");
    });

    future1.get();
    future2.get();

    executorService.shutdownNow();
  }

  @Test(timeout = 10 * 1000L) // 10s
  public void testNoAvailableConnectionAfterWait() throws Exception {
    maxConnections = 1;
    maxWaitTime = Duration.standardSeconds(2).getMillis();
    initializeDbPoolManager();

    executorService = Executors.newFixedThreadPool(2);

    // get and return the connection after three seconds
    int returnAfterSeconds = 5;
    Future future1 = executorService.submit(() -> {
      LOG.info("First task started");

      // get connection
      IDatabase1 connections = dbPoolManager.getConnection();
      assertEquals(0, dbPoolManager.getConnectionPool().getNumIdle());

      // return connection after sleep
      long startTime = System.currentTimeMillis();
      sleep(returnAfterSeconds);
      dbPoolManager.returnConnection(connections);
      long endTime = System.currentTimeMillis();

      // check connection use time
      int returnTime = Duration.millis(endTime - startTime).toStandardSeconds().getSeconds();
      LOG.info("First task returned DB connection in {} seconds", returnTime);
      assertTrue(returnTime >= returnAfterSeconds);

      LOG.info("First task completed");
    });

    int sleepSeconds = 1;
    sleep(sleepSeconds);

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
        long endTime = System.currentTimeMillis();
        long waitMillis = endTime - startTime;
        LOG.info("Second task DB waited for connection: {} seconds", maxWaitTime / 1000);
        assertTrue(waitMillis >= (maxWaitTime - 50) && waitMillis <= (maxWaitTime + 50));
      }

      LOG.info("Second task completed");
    });

    future1.get();
    future2.get();

    executorService.shutdownNow();
  }

  @Test(timeout = 10 * 1000L) // 10s
  public void testKeepAliveTime() throws Exception {
    maxConnections = 7;
    minIdleConnections = 5;
    keepAliveTime = Duration.standardSeconds(2).getMillis();
    initializeDbPoolManager();

    // create and return maxConnections
    List<IDatabase1> connections = Lists.newLinkedList();
    for (int i = 0; i < maxConnections; ++i) {
      connections.add(dbPoolManager.getConnection());
    }
    assertEquals(0, dbPoolManager.getConnectionPool().getNumIdle());
    for (IDatabase1 connection : connections) {
      dbPoolManager.returnConnection(connection);
      LOG.info("Idle connections: {}", dbPoolManager.getConnectionPool().getNumIdle());
    }

    // all idle connections should be available
    assertEquals(maxConnections, dbPoolManager.getConnectionPool().getNumIdle());

    long startTime = System.currentTimeMillis();
    while (true) {
      if (dbPoolManager.getConnectionPool().getNumIdle() == minIdleConnections) {
        break;
      }
    }
    long evictSeconds = System.currentTimeMillis() - startTime;
    LOG.info("Idle connections are evicted after {} seconds", evictSeconds / 1000);
    assertTrue(evictSeconds <= (keepAliveTime + 3000) && evictSeconds >= (keepAliveTime - 3000));
  }

  private void sleep(int seconds) {
    try {
      Thread.sleep(Duration.standardSeconds(seconds).getMillis());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
