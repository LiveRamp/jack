package com.rapleaf.jack.store.executors;

import java.util.List;

import org.junit.Test;

import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.test_project.database_1.models.TestStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestScopeCreationExecutor extends BaseExecutorTestCase {

  @Test
  public void testRootScope() throws Exception {
    createScope("scope0");
    createScope("scope1");

    List<TestStore> records = transactor.query(db -> db.testStore().query().orderByScope().find());
    assertEquals(2, records.size());

    for (int i = 0; i < records.size(); ++i) {
      TestStore scope = records.get(i);

      assertNull(scope.getScope());
      assertEquals(JsConstants.SCOPE_TYPE, scope.getType());
      assertEquals(JsConstants.SCOPE_KEY, scope.getKey());
      assertEquals("scope" + i, scope.getValue());
    }
  }

  @Test
  public void testDuplicatedCreation() throws Exception {
    createScope("scope");
    createScope("scope");

    int scopeCount = transactor.query(db -> db.createQuery().from(TestStore.TBL).fetch().size());
    assertEquals(1, scopeCount);
  }

  @Test
  public void testNestedScope() throws Exception {
    createScope(list("scope0"), "scope1");
    createScope(list("scope0"), "scope2");

    List<TestStore> records = transactor.query(db ->
        db.testStore().query().orderByScope().find()
    );
    assertEquals(3, records.size());

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
