package com.rapleaf.jack.store;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestJsScope {

  @Test
  public void testRootScope() {
    JsScope rootScope = JsScope.root();
    assertTrue(rootScope.isRootScope());
    assertEquals(JsConstants.ROOT_SCOPE_ID, rootScope.getScopeId());
    assertEquals(JsConstants.ROOT_SCOPE_NAME, rootScope.getScopeName());
  }

}
