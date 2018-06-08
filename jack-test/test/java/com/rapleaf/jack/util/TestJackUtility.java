package com.rapleaf.jack.util;


import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestJackUtility {
  @Test
  public void testDateTimeFormat() {
    assertEquals(
        "2018-01-15 18:15:23",
        JackUtility.FORMATTER_FUNCTION_MAP.get(java.sql.Timestamp.class)
            .apply(new DateTime(2018, 1, 15, 18, 15, 23).getMillis())
    );
  }

  @Test
  public void testDateFormat() {
    assertEquals(
        "2018-01-15",
        JackUtility.FORMATTER_FUNCTION_MAP.get(java.sql.Date.class)
            .apply(new DateTime(2018, 1, 15, 0, 0, 0).getMillis())
    );
  }
}
