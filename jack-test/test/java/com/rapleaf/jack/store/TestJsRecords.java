package com.rapleaf.jack.store;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestJsRecords {

  @Test
  public void testEmptyRecords() throws Exception {
    long record = 51L;
    JsRecords emptyRecords = JsRecords.empty(record);
    assertTrue(emptyRecords.isEmpty());
    assertEquals(record, emptyRecords.getParentRecordId().longValue());
  }

}
