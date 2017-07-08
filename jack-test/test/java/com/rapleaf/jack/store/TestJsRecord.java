package com.rapleaf.jack.store;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestJsRecord {

  @Test
  public void testEmptyRecord() throws Exception {
    long recordId = 51L;
    JsRecord emptyRecord = JsRecord.empty(recordId);
    assertTrue(emptyRecord.isEmpty());
    assertEquals(recordId, emptyRecord.getRecordId().longValue());
  }

}
