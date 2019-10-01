package com.rapleaf.jack.transaction;

import java.sql.SQLRecoverableException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.util.TestQueryRetryPolicy;

public class TestDbTransactorImplRetry extends TestQueryRetryPolicy {

  private TransactorImpl.Builder<IDatabase1> transactorBuilder = new DatabasesImpl().getDatabase1Transactor();

  @Before
  public void prepare() throws Exception {
    transactorBuilder.get().query(IDb::deleteAll);
  }

  @Override
  @Test(expected = SqlExecutionFailureException.class)
  public void testNoRetriesOnFailure() {
    super.testNoRetriesOnFailure();
  }

  @Override
  @Test(expected = SqlExecutionFailureException.class)
  public void testFailuresExceedingMaxRetries() {
    super.testFailuresExceedingMaxRetries();
  }

  @Override
  @Test(expected = SqlExecutionFailureException.class)
  public void testTwiceTheFailuresAsMaxRetries() {
    super.testTwiceTheFailuresAsMaxRetries();
  }

  @Override
  protected void testRetriesInternal(int numFailures, int maxRetries) {
    AtomicInteger failCount = new AtomicInteger(0);
    AtomicInteger queryCount = new AtomicInteger(0);
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    ITransactor.RetryPolicy policy = Mockito.mock(ITransactor.RetryPolicy.class);
    IQuery<IDatabase1, Integer> query = Mockito.mock(IQuery.class);

    Mockito.doAnswer(x -> {
      if (failCount.incrementAndGet() > maxRetries) {
        throw new SqlExecutionFailureException(x.getArgument(0));
      }
      return null;
    }).when(policy).onFailure(Mockito.any(Exception.class));
    try {
      Mockito.doAnswer(x -> {
        if (queryCount.getAndIncrement() < numFailures) {
          throw new SQLRecoverableException("Query Failed");
        }
        return 0;
      }).when(query).query(Mockito.any(IDatabase1.class));
    } catch (Exception e) {
      /* This should not really happen */
      throw new RuntimeException(e);
    }
    try {
      transactor.allowRetries(policy).query(query);
      Mockito.verify(policy, Mockito.times(numFailures)).onFailure(Mockito.any(SQLRecoverableException.class));
      Mockito.verify(policy, Mockito.times(1)).onSuccess();
    } catch (SqlExecutionFailureException se) {
      Mockito.verify(policy, Mockito.times(maxRetries + 1))
          .onFailure(Mockito.any(SQLRecoverableException.class));
      throw se;
    } finally {
      int numExecutions = Math.min(numFailures, maxRetries) + 1;
      Mockito.verify(policy, Mockito.times(numExecutions)).execute();
    }
  }
}
