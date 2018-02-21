package com.rapleaf.jack.transaction;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.exception.NoAvailableConnectionException;
import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestDbTransactorImpl extends JackTestCase {
  private TransactorImpl.Builder<IDatabase1> transactorBuilder = new DatabasesImpl().getDatabase1Transactor();
  private ExecutorService executorService;

  @Before
  public void prepare() throws Exception {
    transactorBuilder.get().query(IDb::deleteAll);
  }

  @After
  public void cleanup() throws Exception {
    executorService = null;
  }

  @Test
  public void testSingleExecution() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    String expectedBio = "test";
    String actualBio = transactor.query(db -> {
      User user = db.users().createDefaultInstance();
      user.setBio(expectedBio).save();
      return user.getBio();
    });

    assertEquals(expectedBio, actualBio);
  }

  @Test
  public void testExecutionContextIsResetAfterExecution() throws Exception {

  }

  @Test
  public void testSingleExecutionAsTransactionOldIface() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    String expecetedBio = "test";

    // old transaction iface
    String actualBio = transactor.queryAsTransaction(db -> {
      User user = db.users().createDefaultInstance();
      user.setBio(expecetedBio).save();
      return user.getBio();
    });

    assertEquals(expecetedBio, actualBio);
  }

  @Test
  public void testSingleExecutionAsTransaction() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    String expecetedBio = "test";

    // old transaction iface
    String actualBio = transactor.asTransaction().query(db -> {
      User user = db.users().createDefaultInstance();
      user.setBio(expecetedBio).save();
      return user.getBio();
    });

    assertEquals(expecetedBio, actualBio);
  }

  @Test(expected = SqlExecutionFailureException.class)
  public void testSqlException() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    transactor.execute((IExecution<IDatabase1>)db -> {
      throw new IOException();
    });
  }

  @Test(timeout = 5 * 1000l)
  public void testTransactionSucceedsAfterTransientFailure() throws IOException {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    String expectedBio = "mygreatbio";
    AtomicInteger attemptsMade = new AtomicInteger(0);
    int maxRetries = 3;

    List<User> users = transactor.withMaxRetry(maxRetries)
        .asTransaction()
        .query(db -> {
          db.users().createDefaultInstance().setBio(expectedBio).save();
          if (attemptsMade.get() < maxRetries - 1) {
            attemptsMade.incrementAndGet();
            throw new SqlExecutionFailureException(new Exception());
          }
          return db.users().findAll();
        });

    assertEquals(1, users.size());
    assertEquals(expectedBio, users.get(0).getBio());
  }

  @Test
  public void testTransactionRollbackWithRetry() throws IOException {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    String expectedBio = "mygreatbio";
    int maxRetries = 3;

    try {
      transactor.withMaxRetry(maxRetries)
          .asTransaction()
          .query(db -> {
            db.users().createDefaultInstance().setBio(expectedBio).save();
            throw new SqlExecutionFailureException(new Exception());
          });
    } catch (Exception e) {
      // failure of transactions should leave the db unchanged
    }

    List<User> users = transactor.query(db -> db.users().findAll());
    assertEquals(0, users.size());
  }

  @Test(expected = SqlExecutionFailureException.class, timeout = 5 * 1000l)
  public void testSingleQueryRetryFailsAfterMaxRetries() throws IOException {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    String expectedBio = "mygreatbio";

    transactor.execute(db -> db.users().createDefaultInstance().setBio(expectedBio).save());
    transactor.withMaxRetry(3).query(db -> {
      throw new SqlExecutionFailureException(new IOException());
    });

    // shouldn't execute this line below since a failure above should have happened
    fail();
  }

  @Test
  public void testSingleQueryRetrySucceedsAfterTransientFailure() throws IOException {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    String expectedBio = "mygreatbio";
    int numRetries = 3;
    AtomicInteger attemptsMade = new AtomicInteger(0);

    transactor.execute(db -> db.users().createDefaultInstance().setBio(expectedBio).save());

    List<User> users = transactor.withMaxRetry(numRetries).query(db -> {
      // max out the number of retries; the last query should be able to succeed
      if (attemptsMade.get() < numRetries - 1) {
        attemptsMade.getAndIncrement();
        throw new SqlExecutionFailureException(new IOException());
      } else {
        return db.users().findAll();
      }
    });

    assertEquals(1, users.size());
    assertEquals(expectedBio, users.get(0).getBio());
  }

  @Test
  public void testTransactionRollbackSqlExceptionOldIface() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    String originalBio = "original";
    User user = transactor.queryAsTransaction(db -> {
      User u = db.users().createDefaultInstance();
      u.setBio(originalBio).save();
      return u;
    });

    assertEquals(originalBio, transactor.query(db -> db.users().find(user.getId()).getBio()));

    try {
      transactor.executeAsTransaction((IExecution<IDatabase1>)db -> {
        String newBio = "new";
        user.setBio(newBio).save();
        // within the transaction, the change is visible
        assertEquals(newBio, db.users().find(user.getId()).getBio());
        throw new IOException();
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      // after rollback, there is no change
      assertEquals(originalBio, transactor.query(db -> db.users().find(user.getId()).getBio()));
    }
  }

  @Test
  public void testTransactionRollbackSqlException() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    String originalBio = "original";
    User user = transactor.asTransaction().query(db -> {
      User u = db.users().createDefaultInstance();
      u.setBio(originalBio).save();
      return u;
    });

    assertEquals(originalBio, transactor.query(db -> db.users().find(user.getId()).getBio()));

    try {
      transactor.asTransaction().execute(db -> {
        String newBio = "new";
        user.setBio(newBio).save();
        // within the transaction, the change is visible
        assertEquals(newBio, db.users().find(user.getId()).getBio());
        throw new IOException();
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      // after rollback, there is no change
      assertEquals(originalBio, transactor.query(db -> db.users().find(user.getId()).getBio()));
    }
  }


  @Test(timeout = 5 * 1000) // 5s
  public void testSimultaneousQuery() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(5).get();

    executorService = Executors.newFixedThreadPool(2);

    // each execution should use its own db connection, and not affected by
    // MySQL #67760 (https://bugs.mysql.com/bug.php?id=67760)
    int createCount1 = 100;
    int createCount2 = 50;
    int queryCount = 100;

    Future future1 = executorService.submit(() -> transactor.execute(db -> {
      for (int i = 0; i < createCount1; ++i) {
        db.users().createDefaultInstance().setSomeDatetime(System.currentTimeMillis()).save();
      }
    }));

    Future future2 = executorService.submit(() -> transactor.execute(db -> {
      for (int i = 0; i < queryCount; ++i) {
        db.users().query().whereSomeDatetime(JackMatchers.lessThan(System.currentTimeMillis())).find();
      }
    }));

    Future future3 = executorService.submit(() -> transactor.execute(db -> {
      for (int i = 0; i < createCount2; ++i) {
        db.users().createDefaultInstance().setCreatedAtMillis(System.currentTimeMillis()).save();
      }
    }));

    Future future4 = executorService.submit(() -> transactor.execute(db -> {
      for (int i = 0; i < queryCount; ++i) {
        db.users().query().whereSomeDatetime(JackMatchers.lessThan(System.currentTimeMillis())).find();
      }
    }));

    future1.get();
    future2.get();
    future3.get();
    future4.get();
    executorService.shutdownNow();

    int actualCount = transactor.query(db -> db.createQuery().from(User.TBL).fetch().size());
    assertEquals(createCount1 + createCount2, actualCount);
  }

  @Test
  public void testWaitForConnection() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(1).get();

    executorService = Executors.newFixedThreadPool(2);

    String handle1 = "handle 1";
    String handle2 = "handle 2";
    String handle3 = "handle 3";

    User user = transactor.query(db -> {
      User u = db.users().createDefaultInstance();
      u.setHandle(handle1).save();
      return u;
    });

    Future future1 = executorService.submit(() -> transactor.execute(db -> {
      assertEquals(handle1, db.users().find(user.getId()).getHandle());
      db.users().find(user.getId()).setHandle(handle2).save();
      // block the connection
      sleepMillis(1000);
    }));

    // ensure thread 2 starts after thread 1
    sleepMillis(500);

    // since there is only one db connection, thread 2 will only start
    // after thread 1 has completed.
    Future future2 = executorService.submit(() -> transactor.execute(db -> {
      assertEquals(handle2, db.users().find(user.getId()).getHandle());
      db.users().find(user.getId()).setHandle(handle3).save();
    }));

    future1.get();
    future2.get();
    executorService.shutdownNow();

    assertEquals(handle3, transactor.query(db -> db.users().find(user.getId()).getHandle()));
  }

  @Test
  public void testWaitForConnectionTimeout() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.setMaxTotalConnections(1).setMaxWaitTime(Duration.millis(500)).get();

    executorService = Executors.newFixedThreadPool(2);

    // thread 1 occupies the only connection for longer than maxWaitTime
    Future future1 = executorService.submit(() -> transactor.execute(db -> {
      sleepMillis(1000);
    }));

    sleepMillis(100);

    // thread 2 times out waiting for connection
    Future future2 = executorService.submit(() -> {
      try {
        transactor.execute(db -> {
        });
        fail();
      } catch (NoAvailableConnectionException e) {
        // expected
      }
    });

    future1.get();
    future2.get();
    executorService.shutdownNow();
  }

  @Test(expected = IllegalStateException.class)
  public void testClose() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    transactor.close();
    transactor.execute(db -> {
    });
  }

  @Test
  public void testResetAutoCommit() throws Exception {
    // create a transactor that returns an existing db connection
    IDatabase1 testDbInstance = new DatabasesImpl().getDatabase1();
    ITransactor<IDatabase1> transactor = TransactorImpl.create(() -> testDbInstance).setMaxTotalConnections(1).get();

    transactor.execute(db -> {
      assertTrue(db.getAutoCommit());
      assertTrue(testDbInstance.getAutoCommit());
      db.setAutoCommit(false);
      assertFalse(db.getAutoCommit());
      assertFalse(testDbInstance.getAutoCommit());
    });
    // after execution, the connection auto commit status should have been reset
    assertTrue(testDbInstance.getAutoCommit());
  }

  @Test
  public void testResetBulkOperation() throws Exception {
    // create a transactor that returns an existing db connection
    IDatabase1 testDbInstance = new DatabasesImpl().getDatabase1();
    ITransactor<IDatabase1> transactor = TransactorImpl.create(() -> testDbInstance).setMaxTotalConnections(1).get();

    transactor.execute(db -> {
      assertFalse(db.getBulkOperation());
      assertFalse(testDbInstance.getBulkOperation());
      db.setBulkOperation(true);
      assertTrue(db.getBulkOperation());
      assertTrue(testDbInstance.getBulkOperation());
    });
    // after execution, the connection bulk operation status should have been reset
    assertFalse(testDbInstance.getBulkOperation());
  }

  @Test
  public void testExecutionContextResetsAfterOperation() throws IOException {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();
    transactor.asTransaction().withMaxRetry(5);
    transactor.execute(db -> db.users().createDefaultInstance());

    ExecutionContext context = transactor.getExecutionContext();
    assertEquals(false, context.isAsTransaction());
    assertEquals(ExecutionContext.DEFAULT_RETRY_TIMES, context.getMaxRetries());
  }
}
