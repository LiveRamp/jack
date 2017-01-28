package com.rapleaf.jack.test_project.database_1.transaction;

import java.util.LinkedList;
import java.util.concurrent.Callable;

import org.joda.time.Duration;

import com.rapleaf.jack.exception.TransactionCreationFailureException;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.transaction.ITransactionGroup;

public class Database1TransactionGroup implements ITransactionGroup<Database1Transaction> {

  private final Callable<IDatabase1> dbConstruction;
  private final int maxConnections;
  private final Duration timeout;
  private final LinkedList<IDatabase1> allDbs;
  private final LinkedList<IDatabase1> availableDbs;

  public Database1TransactionGroup(Callable<IDatabase1> callable, int maxConnections, Duration timeout) {
    this.dbConstruction = callable;
    this.maxConnections = maxConnections;
    this.timeout = timeout;
    this.allDbs = new LinkedList<>();
    this.availableDbs = new LinkedList<IDatabase1>();
  }

  @Override
  public Database1Transaction createTransaction() throws TransactionCreationFailureException {
    long timeoutThreshold = System.currentTimeMillis() + timeout.getMillis();

    synchronized (availableDbs) {
      do {
        if (availableDbs.size() > 0) {
          return new Database1Transaction(availableDbs.remove());
        } else if (allDbs.size() < maxConnections) {
          try {
            IDatabase1 database1 = dbConstruction.call();
            allDbs.add(database1);
            return new Database1Transaction(database1);
          } catch (Exception e) {
            throw new TransactionCreationFailureException(IDatabase1.class.getSimpleName() + " construction failed", e);
          }
        }
      } while (System.currentTimeMillis() < timeoutThreshold);
    }

    throw new TransactionCreationFailureException("Transaction creation timeout after " + timeout.getStandardSeconds() + " seconds");
  }

}
