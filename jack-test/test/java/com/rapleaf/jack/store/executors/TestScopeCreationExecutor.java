package com.rapleaf.jack.store.executors;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JackStore;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.test_project.DatabasesImpl;
import com.rapleaf.jack.test_project.database_1.IDatabase1;
import com.rapleaf.jack.test_project.database_1.models.TestStore;
import com.rapleaf.jack.transaction.IExecution;
import com.rapleaf.jack.transaction.ITransactor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestScopeCreationExecutor extends BaseExecutorTestCase {

  @Test
  public void testRootScope() throws Exception {
    jackStore.withinRoot().createScope("scope0").execute();
    jackStore.withinRoot().createScope("scope1").execute();

    List<TestStore> records = transactor.query(db ->
        db.testStore().query().orderByScope().find()
    );

    for (int i = 0; i < records.size(); ++i) {
      TestStore scope = records.get(i);

      assertNull(scope.getScope());
      assertEquals(JsConstants.SCOPE_TYPE, scope.getType());
      assertEquals(JsConstants.SCOPE_KEY, scope.getKey());
      assertEquals("scope" + i, scope.getValue());
    }
  }

  @Test
  public void testNestedScope() throws Exception {
    jackStore.within("scope0").createScope("scope1").execute();
    jackStore.within("scope0").createScope("scope2").execute();

    List<TestStore> records = transactor.query(db ->
        db.testStore().query().orderByScope().find()
    );

    TestStore scope0 = records.get(0);
    assertEquals(JsConstants.SCOPE_TYPE, scope0.getType());
    assertEquals(JsConstants.SCOPE_KEY, scope0.getKey());
    assertEquals("scope0", scope0.getValue());

    for (int i = 1; i < records.size(); ++i) {
      TestStore scope = records.get(i);

      assertEquals(String.valueOf(scope0.getId()), scope.getScope());
      assertEquals(JsConstants.SCOPE_TYPE, scope.getType());
      assertEquals(JsConstants.SCOPE_KEY, scope.getKey());
      assertEquals("scope" + i, scope.getValue());
    }
  }

}
