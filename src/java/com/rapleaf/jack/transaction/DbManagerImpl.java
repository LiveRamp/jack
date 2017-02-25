package com.rapleaf.jack.transaction;

import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.Duration;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.ConnectionClosureFailureException;
import com.rapleaf.jack.exception.ConnectionCreationFailureException;
import com.rapleaf.jack.exception.NoAvailableConnectionException;
import com.rapleaf.jack.exception.SqlExecutionFailureException;

class DbManagerImpl<DB extends IDb> implements IDbManager<DB> {

  private final Callable<DB> dbConstructor;
  private final int maxConnections;
  private final Duration timeout;

  private final LinkedList<DB> allConnections = new LinkedList<>();
  private final LinkedList<DB> idleConnections = new LinkedList<>();

  private boolean closed = false;
  private final Lock lock = new ReentrantLock();
  private final Condition newConnection = lock.newCondition();

  private DbManagerImpl(Callable<DB> dbConstructor, int maxConnections, Duration timeout) {
    this.dbConstructor = dbConstructor;
    this.maxConnections = maxConnections;
    this.timeout = timeout;
  }

  public static <DB extends IDb> DbManagerImpl<DB> create(Callable<DB> dbConstructor, int maxConnections, Duration timeout) {
    return new DbManagerImpl<DB>(dbConstructor, maxConnections, timeout);
  }

  @Override
  public DB getConnection(long timestamp) {
    long timeoutThreshold = timestamp + timeout.getMillis();

    try {
      if (lock.tryLock(timeout.getMillis(), TimeUnit.MILLISECONDS)) {
        try {
          // check for close before waiting
          if (closed) {
            throw new IllegalStateException("DB manager has been closed.");
          }

          // when no connection is available, no new connection can be created and within timeout threshold
          while (idleConnections.isEmpty() && allConnections.size() >= maxConnections && System.currentTimeMillis() < timeoutThreshold) {
            try {
              newConnection.awaitUntil(new Date(timeoutThreshold));
            } catch (InterruptedException e) {
              throw new SqlExecutionFailureException("Transaction pending for connection is interrupted ", e);
            }
          }

          // check for close after waiting
          if (closed) {
            throw new IllegalStateException("DB manager has been closed.");
          }

          // when no connection is available and no new connection can be created
          if (!idleConnections.isEmpty()) {
            return idleConnections.remove();
          }

          // when no connection is available but new connection can be created
          if (allConnections.size() < maxConnections) {
            try {
              DB newConnection = dbConstructor.call();
              newConnection.disableCaching();
              allConnections.add(newConnection);
              return newConnection;
            } catch (Exception e) {
              throw new ConnectionCreationFailureException("DB connection creation failed", e);
            }
          }
        } finally {
          lock.unlock();
        }
      }
      throw new NoAvailableConnectionException("No available connection after waiting for " + timeout.getStandardSeconds() + " seconds");
    } catch (InterruptedException e) {
      throw new ConnectionCreationFailureException("DB connection creation has been interrupted.", e);
    }
  }

  @Override
  public void returnConnection(DB connection) {
    lock.lock();
    try {
      idleConnections.add(connection);
      newConnection.signalAll();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void close() {
    lock.lock();
    Exception lastException = null;
    int failed = 0;

    try {
      closed = true;

      for (DB connection : allConnections) {
        try {
          connection.close();
        } catch (Exception e) {
          ++failed;
          lastException = e;
        }
      }

      if (lastException != null) {
        throw new ConnectionClosureFailureException(
            String.format("%d out of %d DB closure(s) failed; last exception: ", failed, allConnections.size()),
            lastException
        );
      }
    } finally {
      lock.unlock();
    }
  }

}
