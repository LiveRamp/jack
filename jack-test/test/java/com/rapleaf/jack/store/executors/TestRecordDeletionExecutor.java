package com.rapleaf.jack.store.executors;

import java.util.Random;

import org.junit.Test;

import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.test_project.database_1.models.TestStore;

import static org.junit.Assert.assertEquals;

public class TestRecordDeletionExecutor extends BaseExecutorTestCase {

  private static final String KEY = "key";
  private static final String VALUE = "value";

  @Test
  public void testDeletion() throws Exception {
    jackStore.within("scope1", "scope2").indexRecord().put(KEY, VALUE).execute();
    assertEquals(1, transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(KEY)).fetch()).size());
    assertEquals(2, transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(JsConstants.SCOPE_KEY)).fetch()).size());

    jackStore.within("scope1", "scope2").deleteRecord().delete(KEY).execute();
    assertEquals(0, transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(KEY)).fetch()).size());
    assertEquals(2, transactor.query(db -> db.createQuery().from(TestStore.TBL).where(TestStore.KEY.equalTo(JsConstants.SCOPE_KEY)).fetch()).size());
  }

  @Test
  public void testDeleteNothing() throws Exception {
    int random = new Random(System.currentTimeMillis()).nextInt(10);
    for (int i = 0; i < random; ++i) {
      jackStore.withinRoot().indexRecord().put(KEY + i, VALUE + i).execute();
    }
    jackStore.withinRoot().deleteRecord().delete(KEY + random).execute();
    assertEquals(random, transactor.query(db -> db.createQuery().from(TestStore.TBL).fetch()).size());
  }

}
