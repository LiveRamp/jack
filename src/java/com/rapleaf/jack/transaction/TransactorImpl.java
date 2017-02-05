package com.rapleaf.jack.transaction;

import java.io.IOException;
import java.util.concurrent.Callable;

import com.google.common.base.Preconditions;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.TransactionFailureException;

public class TransactorImpl<DB extends IDb> implements ITransactor<DB> {
  private static final Logger LOG = LoggerFactory.getLogger(TransactorImpl.class);

  private final IDbManager<DB> dbManager;

  private TransactorImpl(Callable<DB> callable, int maxConnections, Duration timeout) {
    this.dbManager = maxConnections <= 0 ?
        new FlexibleDbManager<DB>(callable, timeout) :
        new FixedDbManager<DB>(callable, maxConnections, timeout);
  }

  public static <DB extends IDb> Builder<DB> create(Callable<DB> dbConstructor) {
    return new Builder<>(dbConstructor);
  }

  @Override
  public <T> T query(IQuery<DB, T> query) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    try {
      System.out.printf("[%d] Transaction commit complete\n", timestamp);
      T value = query.query(connection);
      connection.commit();
      return value;
    } catch (Exception e) {
      System.out.printf("[%d] Transaction failed\n", timestamp);
      connection.rollback();
      throw new TransactionFailureException(e);
    } finally {
      dbManager.returnConnection(connection);
      System.out.printf("[%d] Transaction returned\n", timestamp);
    }
  }

  @Override
  public void execute(IExecution<DB> execution) {
    long timestamp = System.currentTimeMillis();
    DB connection = dbManager.getConnection(timestamp);
    try {
      execution.execute(connection);
      connection.commit();
      System.out.printf("[%d] Transaction commit complete\n", timestamp);
    } catch (Exception e) {
      connection.rollback();
      System.out.printf("[%d] Transaction failed\n", timestamp);
      throw new RuntimeException(e);
    } finally {
      dbManager.returnConnection(connection);
      System.out.printf("[%d] Transaction returned\n", timestamp);
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
