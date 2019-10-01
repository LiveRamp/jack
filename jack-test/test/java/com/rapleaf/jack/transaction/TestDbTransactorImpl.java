package com.rapleaf.jack.transaction;

import java.io.IOException;
import java.sql.SQLRecoverableException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.IDb;
import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.MysqlDatabaseConnection;
import com.rapleaf.jack.exception.NoAvailableConnectionException;
import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.impl.Database1Impl;
import com.rapleaf.jack.test_project.database_1.models.User;
import com.rapleaf.jack.tracking.NoOpAction;

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
  public void testConnectionInvalidatedOnRollbackFailure() {
    final MysqlDatabaseConnection realConnection = new MysqlDatabaseConnection("database1");
    final BaseDatabaseConnection sBaseDatabaseConnection = Mockito.spy(realConnection);
    final IDatabase1 database1 =
        new Database1Impl(
            sBaseDatabaseConnection,
            new DatabasesImpl(sBaseDatabaseConnection),
            new NoOpAction()
        );

    Mockito.doAnswer(invocationOnMock1 -> {
      throw new SQLRecoverableException("Commit failure");
    }).when(sBaseDatabaseConnection).commit();

    Mockito.doAnswer(invocationOnMock -> {
      throw new SQLRecoverableException("Rollback failure");
    }).when(sBaseDatabaseConnection).rollback();

    final DbPoolManager<IDatabase1> sDbPoolManager = Mockito.spy(
        new DbPoolManager<>(
            () -> database1,
            1,
            1,
            100,
            Integer.MAX_VALUE,
            false
        ));
    final TransactorImpl<IDatabase1> transactor = new TransactorImpl<>(sDbPoolManager, false);

    final int dummyUserId = 1; // This doesn't necessarily exist. We just need some id to execute a query.
    try {
      transactor.asTransaction().query(db -> db.users().find(dummyUserId));
      fail();
    } catch (Exception e) {
      // Ignoring this as we expect an exception to be thrown in the try block.
    }

    Mockito.verify(sDbPoolManager, Mockito.times(1))
        .invalidateConnection(Mockito.any(IDatabase1.class));
    Mockito.verify(sDbPoolManager, Mockito.times(0))
        .returnConnection(Mockito.any(IDatabase1.class));

    // This simply needs to not throw, demonstrating that the connection pool is not exhausted.
    transactor.query(db -> db.users().find(dummyUserId));
  }

  @Test
  public void testConnectionReturnedOnRollbackSuccess() {
    final MysqlDatabaseConnection realConnection = new MysqlDatabaseConnection("database1");
    final BaseDatabaseConnection sBaseDatabaseConnection = Mockito.spy(realConnection);
    final IDatabase1 database1 =
        new Database1Impl(
            sBaseDatabaseConnection,
            new DatabasesImpl(sBaseDatabaseConnection),
            new NoOpAction()
        );

    Mockito.doAnswer(invocationOnMock1 -> {
      throw new SQLRecoverableException("Commit failure");
    }).when(sBaseDatabaseConnection).commit();

    final DbPoolManager<IDatabase1> sDbPoolManager = Mockito.spy(
        new DbPoolManager<>(
            () -> database1,
            1,
            1,
            100,
            Integer.MAX_VALUE,
            false
        ));
    final TransactorImpl<IDatabase1> transactor = new TransactorImpl<>(sDbPoolManager, false);

    final int dummyUserId = 1; // This doesn't necessarily exist. We just need some id to execute a query.
    try {
      transactor.asTransaction().query(db -> db.users().find(dummyUserId));
      fail();
    } catch (Exception e) {
      // Ignoring this as we expect an exception to be thrown in the try block.
    }

    Mockito.verify(sDbPoolManager, Mockito.times(0))
        .invalidateConnection(Mockito.any(IDatabase1.class));
    Mockito.verify(sDbPoolManager, Mockito.times(1))
        .returnConnection(Mockito.any(IDatabase1.class));

    // This simply needs to not throw, demonstrating that the connection pool is not exhausted.
    transactor.query(db -> db.users().find(dummyUserId));
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
  public void testSingleExecutionAsTransaction() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    String expectedBio = "test";
    String actualBio = transactor.asTransaction().query(db -> {
      User user = db.users().createDefaultInstance();
      user.setBio(expectedBio).save();
      return user.getBio();
    });

    assertEquals(expectedBio, actualBio);
  }

  @Test(expected = SqlExecutionFailureException.class)
  public void testSqlException() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    transactor.execute((IExecution<IDatabase1>)db -> {
      throw new IOException();
    });
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
      transactor.asTransaction().execute((IExecution<IDatabase1>)db -> {
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
  public void testTransactionRollbackThrowable() throws Exception {
    TransactorImpl<IDatabase1> transactor = transactorBuilder.get();

    String originalBio = "original";
    User user = transactor.asTransaction().query(db -> {
      User u = db.users().createDefaultInstance();
      u.setBio(originalBio).save();
      return u;
    });

    assertEquals(originalBio, transactor.query(db -> db.users().find(user.getId()).getBio()));

    try {
      transactor.asTransaction().execute((IExecution<IDatabase1>)db -> {
        String newBio = "new";
        user.setBio(newBio).save();
        // within the transaction, the change is visible
        assertEquals(newBio, db.users().find(user.getId()).getBio());
        throw new OutOfMemoryError();
      });
      fail();
    } catch (Throwable t) {
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
    TransactorImpl<IDatabase1> transactor =
        transactorBuilder.setMaxTotalConnections(1).setMaxWaitTime(Duration.ofMillis(500)).get();

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
}
