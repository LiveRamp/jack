package com.rapleaf.jack.transaction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.test_project.database_1.IDatabase1;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class TestMockTransactorImpl {

  private IDatabase1 db;
  private IDbManager<IDatabase1> dbManager;
  private TransactorImpl<IDatabase1> transactor;

  @Before
  @SuppressWarnings("unchecked")
  public void prepare() throws Exception {
    db = mock(IDatabase1.class);
    dbManager = mock(IDbManager.class);
    transactor = new TransactorImpl<>(dbManager);
    when(dbManager.getConnection(anyLong())).thenReturn(db);
    when(db.deleteAll()).thenReturn(true);
  }

  @Test
  public void testMockExecution() throws Exception {
    transactor.execute(IDb::deleteAll);
    verifyExecuteMethod(false);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testMockQuery() throws Exception {
    boolean deleteAll = transactor.execute(IDb::deleteAll);
    assertTrue(deleteAll);
    verifyExecuteMethod(false);
  }

  @Test
  public void testMockExecutionAsTransaction() throws Exception {
    transactor.executeAsTransaction(IDb::deleteAll);
    verifyExecuteMethod(true);
  }

  @Test
  public void testMockQueryAsTransaction() throws Exception {
    boolean deleteAll = transactor.executeAsTransaction(IDb::deleteAll);
    assertTrue(deleteAll);
    verifyExecuteMethod(true);
  }

  private void verifyExecuteMethod(boolean asTransaction) throws Exception {
    boolean autoCommit = !asTransaction;

    InOrder dbMethodOrder = inOrder(dbManager, db);
    dbMethodOrder.verify(dbManager, times(1)).getConnection(anyLong());
    dbMethodOrder.verify(db, times(1)).setAutoCommit(autoCommit);
    dbMethodOrder.verify(db, times(1)).deleteAll();
    if (asTransaction) {
      dbMethodOrder.verify(db, times(1)).commit();
    }
    dbMethodOrder.verify(dbManager, times(1)).returnConnection(db);
  }

}
