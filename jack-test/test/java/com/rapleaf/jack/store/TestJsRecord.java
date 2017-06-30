package com.rapleaf.jack.store;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestJsRecord {

  @Test
  public void testEmptyRecord() throws Exception {
    JsRecord emptyRecord = JsRecord.empty();
    assertTrue(emptyRecord.isEmpty());
    assertEquals(JsConstants.EMPTY_RECORD_SCOPE_ID, emptyRecord.getScopeId().longValue());
  }

}
