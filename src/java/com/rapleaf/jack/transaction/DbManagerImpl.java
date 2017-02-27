package com.rapleaf.jack.transaction;

import java.io.IOException;
import java.util.Date;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.ConnectionClosureFailureException;
import com.rapleaf.jack.exception.ConnectionCreationFailureException;
import com.rapleaf.jack.exception.NoAvailableConnectionException;
import com.rapleaf.jack.exception.SqlExecutionFailureException;

class DbManagerImpl<DB extends IDb> implements IDbManager<DB> {
  private static final Logger LOG = LoggerFactory.getLogger(DbManagerImpl.class);

  private final Duration MAX_IDLE_CONNECTION_CHECK_TIME = Duration.standardSeconds(10);

  private final Callable<DB> dbConstructor;
  private final int coreConnections;
  private final int maxConnections;
  private final Duration waitingTimeout;
  private final Duration keepAliveTime;

  private final Set<DB> busyConnections = Sets.newHashSet();
  private final Queue<DB> idleConnections = Lists.newLinkedList();

  private long lastActiveTimestamp = System.currentTimeMillis();
  private final ScheduledExecutorService idleConnectionTerminator = Executors.newSingleThreadScheduledExecutor();

  private boolean closed = false;
  private final Lock lock = new ReentrantLock();
  private final Condition returnConnection = lock.newCondition();

  private DbManagerImpl(Callable<DB> dbConstructor, int coreConnections, int maxConnections, Duration waitingTimeout, Duration keepAliveTime) {
    this.dbConstructor = dbConstructor;
    this.coreConnections = coreConnections;
    this.maxConnections = maxConnections;
    this.waitingTimeout = waitingTimeout;
    this.keepAliveTime = keepAliveTime;
  }

  public static <DB extends IDb> DbManagerImpl<DB> create(Callable<DB> dbConstructor, int coreConnections, int maxConnections, Duration waitingTimeout, Duration keepAliveTime) {
    return new DbManagerImpl<DB>(dbConstructor, coreConnections, maxConnections, waitingTimeout, keepAliveTime);
  }

  @Override
  public DB getConnection(long timestamp) {
    long timeoutThreshold = timestamp + waitingTimeout.getMillis();

    try {
      if (lock.tryLock(waitingTimeout.getMillis(), TimeUnit.MILLISECONDS)) {
        try {
          if (closed) {
            LOG.error("Cannot get DB connection because DB manager has been closed");
            throw new IllegalStateException("Cannot get DB connection because DB manager has been closed");
          }

          // wait or check for available connections when more than coreConnections connections have been created
          if (busyConnections.size() + idleConnections.size() >= coreConnections) {
            // when no connection is available, no new connection can be created and within timeout threshold
            while (idleConnections.isEmpty() && System.currentTimeMillis() < timeoutThreshold) {
              try {
                returnConnection.awaitUntil(new Date(timeoutThreshold));
              } catch (InterruptedException e) {
                LOG.error("Transaction pending for connection is interrupted ", e);
                throw new SqlExecutionFailureException("Transaction pending for connection is interrupted ", e);
              }
            }

            // check for close after waiting
            if (closed) {
              LOG.error("Cannot get DB connection because DB manager has been closed");
              throw new IllegalStateException("Cannot get DB connection because DB manager has been closed");
            }

            // when no connection is available and no new connection can be created
            if (!idleConnections.isEmpty()) {
              DB connection = idleConnections.remove();
              busyConnections.add(connection);
              return connection;
            }
          }

          // when no connection is available but new connection can be created
          if (busyConnections.size() + idleConnections.size() < maxConnections) {
            try {
              DB newConnection = dbConstructor.call();
              newConnection.disableCaching();
              busyConnections.add(newConnection);
              return newConnection;
            } catch (Exception e) {
              LOG.error("DB connection creation failed", e);
              throw new ConnectionCreationFailureException("DB connection creation failed", e);
            }
          }
        } finally {
          lock.unlock();
        }
      }

      LOG.error("No available connection after waiting for {} seconds", waitingTimeout.getStandardSeconds());
      throw new NoAvailableConnectionException("No available connection after waiting for " + waitingTimeout.getStandardSeconds() + " seconds");
    } catch (InterruptedException e) {
      LOG.error("Waiting for DB connection interrupted", e);
      throw new ConnectionCreationFailureException("Waiting for DB connection interrupted.", e);
    }
  }

  @Override
  public void returnConnection(DB connection) {
    lock.lock();
    try {
      busyConnections.remove(connection);
      idleConnections.add(connection);
      returnConnection.signalAll();
      lastActiveTimestamp = System.currentTimeMillis();
      idleConnectionTerminator.schedule(checkIdleConnection(), keepAliveTime.getMillis(), TimeUnit.MILLISECONDS);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void close() {
    close(Duration.ZERO);
  }

  public void close(Duration timeout) {
    LOG.debug("DB manager close started");

    Exception lastException = null;
    int failed = 0;
    idleConnectionTerminator.shutdownNow();

    lock.lock();
    try {
      closed = true;

      if (timeout.getMillis() > 0 && busyConnections.size() > 0) {
        try {
          returnConnection.await(timeout.getMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          LOG.warn("Waiting for return of DB connection to close DB manager interrupted");
        }
      }

      if (busyConnections.size() > 0) {
        LOG.warn("Closing {} DB connection that are still in use", busyConnections.size());
      }

      for (DB connection : busyConnections) {
        try {
          connection.close();
        } catch (Exception e) {
          LOG.error("DB closure failed", e);
          ++failed;
          lastException = e;
        }
      }
      busyConnections.clear();

      for (DB connection : idleConnections) {
        try {
          connection.close();
        } catch (Exception e) {
          LOG.error("DB closure failed", e);
          ++failed;
          lastException = e;
        }
      }
      idleConnections.clear();

      if (lastException != null) {
        throw new ConnectionClosureFailureException(
            String.format("%d out of %d DB closure(s) failed; last exception: ", failed, busyConnections.size() + idleConnections.size()),
            lastException
        );
      }
    } finally {
      lock.unlock();
    }

    LOG.debug("DB manager close completed");
  }

  private Runnable checkIdleConnection() {
    return () -> {
      // unsafe initial timestamp and connection check without locking
      if (idleConnections.size() <= coreConnections && System.currentTimeMillis() - lastActiveTimestamp < keepAliveTime.getMillis()) {
        return;
      }

      // more stringent check if initial check passes
      try {
        // abort if the lock cannot be acquired in MAX_IDLE_CONNECTION_CHECK_TIME
        if (lock.tryLock(MAX_IDLE_CONNECTION_CHECK_TIME.getMillis(), TimeUnit.MILLISECONDS)) {
          while (idleConnections.size() > coreConnections) {
            try {
              idleConnections.remove().close();
            } catch (IOException e) {
              LOG.error("Failed to close idle connection", e);
              throw new ConnectionClosureFailureException("Failed to close idle connection", e);
            }
          }
        }
      } catch (InterruptedException e) {
        LOG.error("Waiting for lock to close idle connection is interrupted", e);
      }
    };
  }

}
