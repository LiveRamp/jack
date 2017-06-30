package com.rapleaf.jack.store.executors;

import java.util.Optional;

import com.google.common.collect.Lists;
import org.junit.Test;

import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.exceptions.MissingScopeException;

import static org.junit.Assert.assertEquals;

public class TestScopeGetterExecutor extends BaseExecutorTestCase {

  @Test
  public void testGet() throws Exception {
    // within root
    final JsScope scope1 = createScope();
    assertEquals(scope1, transactor.query(db -> jackStore.rootScope().getScope(scope1.getScopeId()).get(db)));
    assertEquals(scope1, transactor.query(db -> jackStore.rootScope().getScope(scope1.getScopeName()).get(db)));

    // within scope
    final JsScope scope2 = createScope(Lists.newArrayList("scope0", "scope1"));
    assertEquals(scope2, transactor.query(db -> jackStore.scope("scope0", "scope1").getScope(scope2.getScopeId()).get(db)));
    assertEquals(scope2, transactor.query(db -> jackStore.scope("scope0", "scope1").getScope(scope2.getScopeName()).get(db)));
  }

  @Test
  public void testOptional() throws Exception {
    // within root
    final JsScope scope1 = createScope();
    assertEquals(Optional.of(scope1), transactor.query(db -> jackStore.rootScope().getScope(scope1.getScopeId()).getOptional(db)));
    assertEquals(Optional.empty(), transactor.query(db -> jackStore.rootScope().getScope(scope1.getScopeId() + 10L).getOptional(db)));
    assertEquals(Optional.empty(), transactor.query(db -> jackStore.rootScope().getScope(scope1.getScopeName() + "0").getOptional(db)));

    // within scope
    final JsScope scope2 = createScope(Lists.newArrayList("scope0", "scope1"));
    assertEquals(Optional.of(scope2), transactor.query(db -> jackStore.scope("scope0", "scope1").getScope(scope2.getScopeId()).getOptional(db)));
    assertEquals(Optional.empty(), transactor.query(db -> jackStore.scope("scope0", "scope1").getScope(scope2.getScopeId() + 10L).getOptional(db)));
    assertEquals(Optional.empty(), transactor.query(db -> jackStore.scope("scope0", "scope1").getScope(scope2.getScopeName() + "0").getOptional(db)));
  }

  @Test(expected = MissingScopeException.class)
  public void testMissingScopeId() throws Exception {
    transactor.execute(db -> {
      jackStore.rootScope().getScope(1L).get(db);
    });
  }

  @Test(expected = MissingScopeException.class)
  public void testMissingScopeName() throws Exception {
    transactor.execute(db -> {
      jackStore.rootScope().getScope("scope0").get(db);
    });
  }

}
