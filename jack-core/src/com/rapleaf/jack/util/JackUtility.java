package com.rapleaf.jack.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;

public final class JackUtility {

  private static final String DATE_FORMAT = "yyyy-MM-dd";
  private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static final Map<Class<?>, Function<Long, String>> FORMATTER_FUNCTION_MAP = ImmutableMap.of(
      java.sql.Date.class, date -> new DateTime(date).toString(DATE_FORMAT),
      java.sql.Timestamp.class, dateTime -> new DateTime(dateTime).toString(TIMESTAMP_FORMAT)
  );

  private JackUtility() {
  }

  public static int safeLongToInt(long l) {
    if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
      throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
    }
    return (int)l;
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
