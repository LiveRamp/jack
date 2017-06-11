package com.rapleaf.jack.store.executors;

import java.util.Optional;

import com.google.common.collect.Lists;
import org.junit.Test;

import com.rapleaf.jack.store.JsScope;

import static org.junit.Assert.assertEquals;

public class TestScopeGetterExecutor extends BaseExecutorTestCase {

  @Test
  public void testGet() throws Exception {
    // within root
    JsScope scope = createScope();
    assertEquals(scope, jackStore.withinRoot().getScope(scope.getScopeId()).get());
    assertEquals(scope, jackStore.withinRoot().getScope(scope.getScopeName()).get());

    // within scope
    scope = createScope(Lists.newArrayList("scope0", "scope1"));
    assertEquals(scope, jackStore.within("scope0", "scope1").getScope(scope.getScopeId()).get());
    assertEquals(scope, jackStore.within("scope0", "scope1").getScope(scope.getScopeName()).get());
  }

  @Test
  public void testOptional() throws Exception {
    // within root
    JsScope scope = createScope();
    assertEquals(Optional.of(scope), jackStore.withinRoot().getScope(scope.getScopeId()).getOptional());
    assertEquals(Optional.empty(), jackStore.withinRoot().getScope(scope.getScopeId() + 10L).getOptional());
    assertEquals(Optional.empty(), jackStore.withinRoot().getScope(scope.getScopeName() + "0").getOptional());

    // within scope
    scope = createScope(Lists.newArrayList("scope0", "scope1"));
    assertEquals(Optional.of(scope), jackStore.within("scope0", "scope1").getScope(scope.getScopeId()).getOptional());
    assertEquals(Optional.empty(), jackStore.within("scope0", "scope1").getScope(scope.getScopeId() + 10L).getOptional());
    assertEquals(Optional.empty(), jackStore.within("scope0", "scope1").getScope(scope.getScopeName() + "0").getOptional());
  }

}
