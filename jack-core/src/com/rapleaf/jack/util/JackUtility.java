package com.rapleaf.jack.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

public final class JackUtility {

  private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

  public static final Function<LocalDateTime, Long> DATETIME_TO_MILLIS = dateTime -> dateTime.atZone(SYSTEM_ZONE).toInstant().toEpochMilli();
  public static final Function<LocalDate, Long> DATE_TO_MILLIS = date -> date.atStartOfDay(SYSTEM_ZONE).toInstant().toEpochMilli();

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public static final Function<String, Long> DATE_STRING_TO_MILLIS = date -> DATE_TO_MILLIS.apply(LocalDate.parse(date, DATE_FORMATTER));
  public static final Function<String, Long> DATETIME_STRING_TO_MILLIS = dateTime -> DATETIME_TO_MILLIS.apply(LocalDateTime.parse(dateTime, TIMESTAMP_FORMATTER));

  public static final Map<Class<?>, Function<Long, String>> FORMATTER_FUNCTION_MAP = ImmutableMap.of(
      java.sql.Date.class, new DateFormatter(DATE_FORMATTER),
      java.sql.Timestamp.class, new DateFormatter(TIMESTAMP_FORMATTER)
  );

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
      return Instant.ofEpochMilli(date).atZone(JackUtility.SYSTEM_ZONE).format(formatter);
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
