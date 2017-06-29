package com.rapleaf.jack.store.executors;

import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Test;

import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsScopes;
import com.rapleaf.jack.test_project.database_1.models.TestStore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestScopeCreationExecutor extends BaseExecutorTestCase {

  @Test
  public void testDefaultRootScope() throws Exception {
    JsScope scope = createScope();
    JsScopes jsScopes = jackStore.rootScope().queryScope().fetch();
    assertEquals(1, jsScopes.size());
    assertEquals(scope.getScopeId(), jsScopes.getScopes().get(0).getScopeId());
    assertEquals(scope.getScopeName(), jsScopes.getScopes().get(0).getScopeName());
  }

  @Test
  public void testDefaultCustomScope() throws Exception {
    JsScope scope = createScope(Lists.newArrayList("scope0", "scope1"));
    JsScopes jsScopes = jackStore.scope("scope0", "scope1").queryScope().fetch();
    assertEquals(1, jsScopes.size());
    assertEquals(scope.getScopeId(), jsScopes.getScopes().get(0).getScopeId());
    assertEquals(scope.getScopeName(), jsScopes.getScopes().get(0).getScopeName());
  }

  @Test
  public void testCustomRootScope() throws Exception {
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
  public void testCustomNestedScope() throws Exception {
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

      assertEquals(scope0.getId(), scope.getScope().longValue());
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

}
