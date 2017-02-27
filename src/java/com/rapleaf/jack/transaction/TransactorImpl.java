package com.rapleaf.jack.transaction;

import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.SqlExecutionFailureException;

public class TransactorImpl<DB extends IDb> implements ITransactor<DB> {
  private static final Logger LOG = LoggerFactory.getLogger(TransactorImpl.class);

  private static int DEFAULT_CORE_CONNECTIONS = 1;
  private static int DEFAULT_MAX_CONNECTIONS = 1;
  private static Duration DEFAULT_WAITING_TIMEOUT = Duration.standardMinutes(1);

  private final IDbManager<DB> dbManager;

  TransactorImpl(IDbManager<DB> dbManager) {
    this.dbManager = dbManager;
  }

  public static <DB extends IDb> Builder<DB> create(Callable<DB> dbConstructor) {
    return new Builder<>(dbConstructor);
  }

  @Override
  public <T> T execute(IQuery<DB, T> query) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    connection.setAutoCommit(true);
    try {
      return query.query(connection);
    } catch (Exception e) {
      LOG.error("SQL execution failure", e);
      throw new SqlExecutionFailureException(e);
    } finally {
      dbManager.returnConnection(connection);
    }
  }

  @Override
  public <T> T executeAsTransaction(IQuery<DB, T> query) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    connection.setAutoCommit(false);
    try {
      T value = query.query(connection);
      connection.commit();
      return value;
    } catch (Exception e) {
      LOG.error("SQL execution failure", e);
      connection.rollback();
      throw new SqlExecutionFailureException(e);
    } finally {
      dbManager.returnConnection(connection);
    }
  }

  @Override
  public void execute(IExecution<DB> execution) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    connection.setAutoCommit(true);
    try {
      execution.execute(connection);
    } catch (Exception e) {
      LOG.error("SQL execution failure", e);
      throw new SqlExecutionFailureException(e);
    } finally {
      dbManager.returnConnection(connection);
    }
  }

  @Override
  public void executeAsTransaction(IExecution<DB> execution) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    connection.setAutoCommit(false);
    try {
      execution.execute(connection);
      connection.commit();
    } catch (Exception e) {
      LOG.error("SQL execution failure", e);
      connection.rollback();
      throw new SqlExecutionFailureException(e);
    } finally {
      dbManager.returnConnection(connection);
    }
  }

  @Override
  public void close() {
    dbManager.close();
  }

  public static class Builder<DB extends IDb> implements ITransactor.Builder<DB, TransactorImpl<DB>> {
    private Callable<DB> dbConstructor;
    private int coreConnections = DEFAULT_CORE_CONNECTIONS;
    private int maxConnections = DEFAULT_MAX_CONNECTIONS;
    private Duration waitingTimeout = DEFAULT_WAITING_TIMEOUT;
    private Duration keepAliveTime = DbManagerImpl.AUTO_CLOSE_IDLE_CONNECTION_THRESHOLD;

    private Builder(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
    }

    public Builder<DB> setDbConstructor(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
      return this;
    }

    public Builder<DB> setConnections(int coreConnections, int maxConnections) {
      Preconditions.checkArgument(coreConnections >= 0, "Core connections must be larger than zero");
      Preconditions.checkArgument(maxConnections >= Math.max(1, coreConnections), "Max connections must be larger than one or core connections");
      this.coreConnections = coreConnections;
      this.maxConnections = maxConnections;
      return this;
    }

    public Builder<DB> setInfiniteConnections() {
      this.maxConnections = 0;
      return this;
    }

    public Builder<DB> setConnectionWaitingTimeout(Duration waitingTimeout) {
      this.waitingTimeout = waitingTimeout;
      return this;
    }

    public Builder<DB> setAutoCloseIdleConnection(Duration keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
      return this;
    }

    @Override
    public TransactorImpl<DB> get() {
      return Builder.get(this);
    }

    private static <DB extends IDb> TransactorImpl<DB> get(TransactorImpl.Builder<DB> builder) {
      return new TransactorImpl<DB>(DbManagerImpl.create(builder.dbConstructor, builder.coreConnections, builder.maxConnections, builder.waitingTimeout, builder.keepAliveTime));
    }
  }

}
