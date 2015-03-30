package com.rapleaf.jack.queries;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Functions {
  private static final DateTimeFormatter datetimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss");
  private static final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

  public static String DATETIME(long datetime) {
    return new DateTime(datetime).toString(datetimeFormat);
  }

  public static Collection<String> DATETIMES(long firstDatetime, long... otherDatetimes) {
    DateTime dt = new DateTime();
    List<String> timestamps = Lists.newArrayList(dt.withMillis(firstDatetime).toString(datetimeFormat));
    for (long datetime : otherDatetimes) {
      timestamps.add(dt.withMillis(datetime).toString(datetimeFormat));
    }
    return timestamps;
  }

  public static Collection<String> DATETIMES(Collection<Long> datetimes) {
    DateTime dt = new DateTime();
    List<String> timestamps = Lists.newArrayList();
    for (Long datetime : datetimes) {
      timestamps.add(dt.withMillis(datetime).toString(datetimeFormat));
    }
    return timestamps;
  }

  public static String DATE(long date) {
    return new DateTime(date).toString(dateFormat);
  }

  public static Collection<String> DATES(long firstDate, long... otherDates) {
    DateTime dt = new DateTime();
    List<String> datestamps = Lists.newArrayList(dt.withMillis(firstDate).toString(dateFormat));
    for (long date : otherDates) {
      datestamps.add(dt.withMillis(date).toString(dateFormat));
    }
    return datestamps;
  }

  public static Collection<String> DATES(Collection<Long> dates) {
    DateTime dt = new DateTime();
    List<String> datestamps = Lists.newArrayList();
    for (Long date : dates) {
      datestamps.add(dt.withMillis(date).toString(dateFormat));
    }
    return datestamps;
  }
}
