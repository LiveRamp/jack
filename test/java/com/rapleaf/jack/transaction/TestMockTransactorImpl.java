package com.rapleaf.jack.transaction;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.JackTestCase;
import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.test_project.database_1.IDatabase1;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestMockTransactorImpl extends JackTestCase {

  private IDatabase1 db;
  private IDbManager<IDatabase1> dbManager;
  private TransactorImpl<IDatabase1> transactor;

  @Before
  @SuppressWarnings("unchecked")
  public void prepare() throws Exception {
    db = mock(IDatabase1.class);
    dbManager = mock(IDbManager.class);
    transactor = new TransactorImpl<>(dbManager);
    when(dbManager.getConnection()).thenReturn(db);
    when(db.deleteAll()).thenReturn(true);
  }

  @Test
  public void testMockExecution() throws Exception {
    transactor.execute(IDb::deleteAll);
    verifyExecuteMethod(false, false);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testMockQuery() throws Exception {
    boolean deleteAll = transactor.execute(IDb::deleteAll);
    assertTrue(deleteAll);
    verifyExecuteMethod(false, false);
  }

  @Test
  public void testMockExecutionAsTransaction() throws Exception {
    transactor.executeAsTransaction(IDb::deleteAll);
    verifyExecuteMethod(true, false);
  }

  @Test
  public void testMockQueryAsTransaction() throws Exception {
    boolean deleteAll = transactor.executeAsTransaction(IDb::deleteAll);
    assertTrue(deleteAll);
    verifyExecuteMethod(true, false);
  }

  @Test
  public void testMockExecutionWithException() throws Exception {
    try {
      transactor.execute((IExecution<IDatabase1>)db -> {
        db.deleteAll();
        throw new IOException();
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      verifyExecuteMethod(false, true);
    }
  }

  @Test
  public void testMockQueryWithException() throws Exception {
    try {
      boolean result = transactor.execute((IQuery<IDatabase1, Boolean>)db -> {
        db.deleteAll();
        throw new IOException();
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      verifyExecuteMethod(false, true);
    }
  }

  @Test
  public void testMockExecutionAsTransactionWithException() throws Exception {
    try {
      transactor.executeAsTransaction((IExecution<IDatabase1>)db -> {
        db.deleteAll();
        throw new IOException();
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      verifyExecuteMethod(true, true);
    }
  }

  @Test
  public void testMockQueryAsTransactionWithException() throws Exception {
    try {
      boolean result = transactor.executeAsTransaction((IQuery<IDatabase1, Boolean>)db -> {
        db.deleteAll();
        throw new IOException();
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      verifyExecuteMethod(true, true);
    }
  }

  private void verifyExecuteMethod(boolean asTransaction, boolean withException) throws Exception {
    boolean autoCommit = !asTransaction;

    InOrder dbMethodOrder = inOrder(dbManager, db);
    dbMethodOrder.verify(dbManager, times(1)).getConnection();
    dbMethodOrder.verify(db, times(1)).setAutoCommit(autoCommit);
    dbMethodOrder.verify(db, times(1)).deleteAll();

    if (asTransaction) {
      if (withException) {
        dbMethodOrder.verify(db, times(1)).rollback();
      } else {
        dbMethodOrder.verify(db, times(1)).commit();
      }
    }

    dbMethodOrder.verify(dbManager, times(1)).returnConnection(db);
  }

  @Test
  public void testMockClose() throws Exception {
    transactor.close();
    verify(dbManager, times(1)).close();
  }

}
