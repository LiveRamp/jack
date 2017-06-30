package com.rapleaf.jack.store.executors;

import java.util.Optional;

import com.google.common.collect.Lists;
import org.junit.Test;

import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.exceptions.MissingScopeException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestScopeGetterExecutor extends BaseExecutorTestCase {

  @Test
  public void testGet() throws Exception {
    // within root
    final JsScope scope1 = createScope();
    assertEquals(scope1, transactor.query(db -> jackStore.rootScope().getSubScope(scope1.getScopeId()).execute(db)));
    assertEquals(scope1, transactor.query(db -> jackStore.rootScope().getSubScope(scope1.getScopeName()).execute(db)));

    // within scope
    final JsScope scope2 = createScope(Lists.newArrayList("scope0", "scope1"));
    assertEquals(scope2, transactor.query(db -> jackStore.scope("scope0", "scope1").getSubScope(scope2.getScopeId()).execute(db)));
    assertEquals(scope2, transactor.query(db -> jackStore.scope("scope0", "scope1").getSubScope(scope2.getScopeName()).execute(db)));
  }

  @Test
  public void testOptional() throws Exception {
    // within root
    final JsScope scope1 = createScope();
    assertEquals(Optional.of(scope1), transactor.query(db -> jackStore.rootScope().getSubScope(scope1.getScopeId()).getOptional(db)));
    assertEquals(Optional.empty(), transactor.query(db -> jackStore.rootScope().getSubScope(scope1.getScopeId() + 10L).getOptional(db)));
    assertEquals(Optional.empty(), transactor.query(db -> jackStore.rootScope().getSubScope(scope1.getScopeName() + "0").getOptional(db)));

    // within scope
    final JsScope scope2 = createScope(Lists.newArrayList("scope0", "scope1"));
    assertEquals(Optional.of(scope2), transactor.query(db -> jackStore.scope("scope0", "scope1").getSubScope(scope2.getScopeId()).getOptional(db)));
    assertEquals(Optional.empty(), transactor.query(db -> jackStore.scope("scope0", "scope1").getSubScope(scope2.getScopeId() + 10L).getOptional(db)));
    assertEquals(Optional.empty(), transactor.query(db -> jackStore.scope("scope0", "scope1").getSubScope(scope2.getScopeName() + "0").getOptional(db)));
  }

  @Test
  public void testMissingScopeId() throws Exception {
    try {
      transactor.execute(db -> {
        jackStore.rootScope().getSubScope(1L).execute(db);
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof MissingScopeException);
    }
  }

  @Test
  public void testMissingScopeName() throws Exception {
    try {
      transactor.execute(db -> {
        jackStore.rootScope().getSubScope("scope0").execute(db);
      });
      fail();
    } catch (SqlExecutionFailureException e) {
      assertTrue(e.getCause() instanceof MissingScopeException);
    }
  }

}
