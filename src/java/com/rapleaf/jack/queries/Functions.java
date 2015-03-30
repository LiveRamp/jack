package com.rapleaf.jack.queries;

import java.text.Format;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.FastDateFormat;

public class Functions {
  private static final Format datetimeFormat = FastDateFormat.getInstance("yyyy-MM-dd hh:mm:ss");
  private static final Format dateFormat = FastDateFormat.getInstance("yyyy-MM-dd");

  public static String DATETIME(long datetime) {
    return datetimeFormat.format(new java.util.Date(datetime));
  }

  public static Collection<String> DATETIMES(long firstDatetime, long... otherDatetimes) {
    List<String> timestamps = Lists.newArrayList(DATETIME(firstDatetime));
    for (long datetime : otherDatetimes) {
      timestamps.add(DATETIME(datetime));
    }
    return timestamps;
  }

  public static Collection<String> DATETIMES(Collection<Long> datetimes) {
    List<String> timestamps = Lists.newArrayList();
    for (Long datetime : datetimes) {
      timestamps.add(DATETIME(datetime));
    }
    return timestamps;
  }

  public static String DATE(long date) {
    return dateFormat.format(date);
  }

  public static Collection<String> DATES(long firstDate, long... otherDates) {
    List<String> datestamps = Lists.newArrayList(DATE(firstDate));
    for (long datetime : otherDates) {
      datestamps.add(DATE(datetime));
    }
    return datestamps;
  }

  public static Collection<String> DATES(Collection<Long> datetimes) {
    List<String> datestamps = Lists.newArrayList();
    for (Long datetime : datetimes) {
      datestamps.add(DATE(datetime));
    }
    return datestamps;
  }

}
