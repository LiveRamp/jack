package com.rapleaf.jack.util;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class JackUtility {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
  public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

  public static final Map<Class, Function<Long, String>> FORMATTER_FUNCTION_MAP = new HashMap<Class, Function<Long, String>>(2) {{
    put(java.sql.Date.class, new DateFormatter(DATE_FORMATTER));
    put(java.sql.Timestamp.class, new DateFormatter(TIMESTAMP_FORMATTER));
  }};

  public static final Function<Object, Long> LONG_CASTER = new Function<Object, Long>() {
    @Override
    public Long apply(final Object value) {
      return Long.class.cast(value);
    }
  };

  private JackUtility() {
  }

  public static int safeLongToInt(long l) {
    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
      throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
    }
    return (int)l;
  }

  private static final class DateFormatter implements Function<Long, String> {

    private final DateTimeFormatter formatter;

    DateFormatter(final DateTimeFormatter formatter) {
      this.formatter = formatter;
    }

    @Override
    public String apply(final Long date) {
      return new DateTime(date).toString(formatter);
    }
  }

}
