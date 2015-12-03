package com.rapleaf.jack.queries;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Date functions are no longer needed. Use plain long parameters for date query.
 * Currently all functions just pass through the input values for backward compatibility.
 * They will be completely removed soon.
 */
@Deprecated
public class Functions {
  private static final DateTimeFormatter datetimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
  private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

  public static Long DATETIME(long datetime) {
    return datetime;
  }

  public static Collection<Long> DATETIMES(long firstDatetime, long... otherDatetimes) {
    List<Long> timestamps = Lists.newArrayList(firstDatetime);
    for (long datetime : otherDatetimes) {
      timestamps.add(datetime);
    }
    return timestamps;
  }

  public static Collection<Long> DATETIMES(Collection<Long> datetimes) {
    List<Long> timestamps = Lists.newArrayList();
    for (Long datetime : datetimes) {
      timestamps.add(datetime);
    }
    return timestamps;
  }

  public static Long DATE(long date) {
    return date;
  }

  public static Collection<Long> DATES(long firstDate, long... otherDates) {
    List<Long> datestamps = Lists.newArrayList(firstDate);
    for (long date : otherDates) {
      datestamps.add(date);
    }
    return datestamps;
  }

  public static Collection<Long> DATES(Collection<Long> dates) {
    return dates;
  }
}
