package com.rapleaf.jack.transaction;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;
import org.joda.time.Duration;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.SqlExecutionFailureException;

public class TransactorImpl<DB extends IDb> implements ITransactor<DB> {

  private final IDbManager<DB> dbManager;

  private TransactorImpl(Callable<DB> callable, int maxConnections, Duration timeout) {
    this.dbManager = maxConnections <= 0 ?
        new FlexibleDbManager<DB>(callable, maxConnections, timeout) :
        new FixedDbManager<DB>(callable, maxConnections, timeout);
  }

  public static <DB extends IDb> Builder<DB> create(Callable<DB> dbConstructor) {
    return new Builder<>(dbConstructor);
  }

  @Override
  public <T> T query(IQuery<DB, T> query) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    connection.setAutoCommit(true);
    try {
      return query.query(connection);
    } catch (Exception e) {
      throw new SqlExecutionFailureException(e);
    } finally {
      dbManager.returnConnection(connection);
    }
  }

  @Override
  public <T> T transQuery(IQuery<DB, T> query) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    connection.setAutoCommit(false);
    try {
      T value = query.query(connection);
      connection.commit();
      return value;
    } catch (Exception e) {
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
      throw new SqlExecutionFailureException(e);
    } finally {
      dbManager.returnConnection(connection);
    }
  }

  @Override
  public void transExecute(IExecution<DB> execution) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    connection.setAutoCommit(false);
    try {
      execution.execute(connection);
      connection.commit();
    } catch (Exception e) {
      connection.rollback();
      throw new SqlExecutionFailureException(e);
    } finally {
      dbManager.returnConnection(connection);
    }
  }

  @Override
  public void close() throws IOException {
    dbManager.close();
  }

  public static class Builder<DB extends IDb> implements ITransactor.Builder<DB, TransactorImpl<DB>> {
    private Callable<DB> dbConstructor;
    private int maxConnections = 1;
    private Duration timeout = Duration.standardSeconds(1L);

    private Builder(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
    }

    public Builder<DB> setDbConstructor(Callable<DB> dbConstructor) {
      this.dbConstructor = dbConstructor;
      return this;
    }

    public Builder<DB> setMaxConnections(int maxConnections) {
      Preconditions.checkArgument(maxConnections > 0);
      this.maxConnections = maxConnections;
      return this;
    }

    public Builder<DB> setInfiniteConnections() {
      this.maxConnections = 0;
      return this;
    }

    public Builder<DB> setTimeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    @Override
    public TransactorImpl<DB> get() {
      return new TransactorImpl<>(dbConstructor, maxConnections, timeout);
    }
  }

}
