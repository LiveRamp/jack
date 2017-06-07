package com.rapleaf.jack.store.executors;

import org.junit.Before;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JackStore;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.TestStore;
import com.rapleaf.jack.transaction.IExecution;
import com.rapleaf.jack.transaction.ITransactor;

public class BaseExecutorTestCase {

  protected final ITransactor<IDatabase1> transactor = new DatabasesImpl().getDatabase1Transactor().get();
  protected final JackStore<IDatabase1> jackStore = new JackStore<>(transactor, TestStore.TBL, TestStore.SCOPE, TestStore.TYPE, TestStore.KEY, TestStore.VALUE);

  @Before
  public void prepare() throws Exception {
    transactor.executeAsTransaction((IExecution<IDatabase1>)IDb::deleteAll);
  }


}
