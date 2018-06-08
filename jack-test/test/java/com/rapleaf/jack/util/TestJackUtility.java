package com.rapleaf.jack.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestJackUtility {
  @Test
  public void testDateTimeToMillis() {
    long millis = System.currentTimeMillis();
    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    assertEquals(millis, JackUtility.DATETIME_TO_MILLIS.apply(dateTime).longValue());
  }

  @Test
  public void testDateToMillis() {
    long millis = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    LocalDate date = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).toLocalDate();
    assertEquals(millis, JackUtility.DATE_TO_MILLIS.apply(date).longValue());
  }

  @Test
  public void testDateFormatter() {
    String dateString = "2018-01-02";
    assertEquals(dateString, LocalDate.parse(dateString).format(JackUtility.DATE_FORMATTER));
  }

  @Test
  public void testTimestampFormatter() {
    String dateTimeString = "2018-01-02 18:30:15";
    assertEquals(dateTimeString, LocalDateTime.parse(dateTimeString, JackUtility.TIMESTAMP_FORMATTER).format(JackUtility.TIMESTAMP_FORMATTER));
  }
}
