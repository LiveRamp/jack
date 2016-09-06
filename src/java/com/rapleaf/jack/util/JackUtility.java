package com.rapleaf.jack.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.rapleaf.jack.queries.Index;

public final class JackUtility {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
  public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

  public static final Map<Class<?>, Function<Long, String>> FORMATTER_FUNCTION_MAP = new HashMap<>(2);

  static {
    FORMATTER_FUNCTION_MAP.put(java.sql.Date.class, new DateFormatter(DATE_FORMATTER));
    FORMATTER_FUNCTION_MAP.put(java.sql.Timestamp.class, new DateFormatter(TIMESTAMP_FORMATTER));
  }

  public static final Function<Object, Long> LONG_CASTER = Long.class::cast;

  public static final Function<Index, String> INDEX_NAME_EXTRACTOR = Index::getName;

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

  public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
    ResultSetMetaData rsmd = rs.getMetaData();
    int columns = rsmd.getColumnCount();
    for (int x = 1; x <= columns; x++) {
      if (columnName.equals(rsmd.getColumnName(x))) {
        return true;
      }
    }
    return false;
  }

}
