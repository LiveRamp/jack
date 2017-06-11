package com.rapleaf.jack.store.executors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsScopes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestScopeQueryExecutor extends BaseExecutorTestCase {

  @Test
  public void testNoConstraint() throws Exception {
    JsScopes scopes = jackStore.withinRoot().queryScope().fetch();
    assertEquals(0, scopes.size());

    // three scopes under root
    JsScope s1 = createScope("1");
    JsScope s2 = createScope("2");
    JsScope s3 = createScope("3");

    scopes = jackStore.withinRoot().queryScope().fetch();
    assertEquals(3, scopes.size());
    assertEquals(Sets.newHashSet(s1, s2, s3), Sets.newHashSet(scopes.getScopes()));

    // three scopes under scope 1
    JsScope s01 = createScope(list("1"), "1");
    JsScope s02 = createScope(list("1"), "2");
    JsScope s03 = createScope(list("1"), "3");

    scopes = jackStore.withinRoot().queryScope().fetch();
    assertEquals(3, scopes.size());
    assertEquals(Sets.newHashSet(s1, s2, s3), Sets.newHashSet(scopes.getScopes()));

    scopes = jackStore.within("1").queryScope().fetch();
    assertEquals(3, scopes.size());
    assertEquals(Sets.newHashSet(s01, s02, s03), Sets.newHashSet(scopes.getScopes()));
  }

  @Test
  public void testScopeConstraint() throws Exception {
    JsScope s1 = createScope("1");
    JsScope s2 = createScope("2");
    JsScope s3 = createScope("3");

    // name constraint
    JsScopes scopes = jackStore.withinRoot().queryScope()
        .whereScopeName(JackMatchers.greaterThan("2"))
        .fetch();
    assertEquals(1, scopes.size());
    assertEquals(s3, scopes.getScopes().get(0));

    scopes = jackStore.withinRoot().queryScope()
        .whereScopeName(JackMatchers.greaterThanOrEqualTo("2"))
        .fetch();
    assertEquals(2, scopes.size());
    assertEquals(Sets.newHashSet(s2, s3), Sets.newHashSet(scopes.getScopes()));

    scopes = jackStore.withinRoot().queryScope()
        .whereScopeName(JackMatchers.isNull())
        .fetch();
    assertTrue(scopes.isEmpty());

    scopes = jackStore.withinRoot().queryScope()
        .whereScopeName(JackMatchers.equalTo("2"))
        .fetch();
    assertEquals(s2, scopes.getScopes().get(0));

    // id constraint
    scopes = jackStore.withinRoot().queryScope()
        .whereScopeId(JackMatchers.equalTo(s2.getScopeId()))
        .fetch();
    assertEquals(s2, scopes.getScopes().get(0));
    
    scopes = jackStore.withinRoot().queryScope()
        .whereScopeId(JackMatchers.lessThanOrEqualTo(s2.getScopeId()))
        .fetch();
    assertEquals(2, scopes.size());
    assertEquals(Sets.newHashSet(s1, s2), Sets.newHashSet(scopes.getScopes()));
  }

  @Test
  public void testOrder() throws Exception {
    JsScope s1 = createScope("b");
    JsScope s2 = createScope("a");
    JsScope s3 = createScope("c");

    // order by id asc
    JsScopes scopes = jackStore.withinRoot().queryScope().orderByScopeId(QueryOrder.ASC).fetch();
    assertEquals(Lists.newArrayList(s1, s2, s3), scopes.getScopes());

    // order by id desc
    scopes = jackStore.withinRoot().queryScope().orderByScopeId(QueryOrder.DESC).fetch();
    assertEquals(Lists.newArrayList(s3, s2, s1), scopes.getScopes());

    // order by name asc
    scopes = jackStore.withinRoot().queryScope().orderByScopeName(QueryOrder.ASC).fetch();
    assertEquals(Lists.newArrayList(s2, s1, s3), scopes.getScopes());

    // order by name desc
    scopes = jackStore.withinRoot().queryScope().orderByScopeName(QueryOrder.DESC).fetch();
    assertEquals(Lists.newArrayList(s3, s1, s2), scopes.getScopes());
  }

  @Test
  public void testLimit() throws Exception {
    JsScope s1 = createScope("1");
    JsScope s2 = createScope("2");
    JsScope s3 = createScope("3");

    JsScopes scopes = jackStore.withinRoot().queryScope().orderByScopeId(QueryOrder.ASC).limit(2).fetch();
    assertEquals(Lists.newArrayList(s1, s2), scopes.getScopes());

    scopes = jackStore.withinRoot().queryScope().orderByScopeId(QueryOrder.ASC).limit(1, 2).fetch();
    assertEquals(Lists.newArrayList(s2, s3), scopes.getScopes());
  }

}
