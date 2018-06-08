package com.rapleaf.jack.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Test;

import com.rapleaf.jack.JackTestCase;

import static org.junit.Assert.assertEquals;

public class TestJackUtility extends JackTestCase {
  // this function is not used in production, but it is used to create
  // mock objects in unit tests; so its correctness is important
  @Test
  public void testDateTimeToMillis() {
    long millis = System.currentTimeMillis();
    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    assertEquals(millis, DATETIME_TO_MILLIS.apply(dateTime).longValue());
  }

  // this function is not used in production, but it is used to create
  // mock objects in unit tests; so its correctness is important
  @Test
  public void testDateToMillis() {
    long millis = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    LocalDate date = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).toLocalDate();
    assertEquals(millis, DATE_TO_MILLIS.apply(date).longValue());
  }

  // this function is not used in production, but it is used to create
  // mock objects in unit tests; so its correctness is important
  @Test
  public void testDateTimeStringToMillis() {
    String dateTimeString = "2018-01-02 18:30:15";
    assertEquals(
        LocalDateTime.of(2018, 1, 2, 18, 30, 15).atZone(SYSTEM_ZONE).toInstant().toEpochMilli(),
        DATETIME_STRING_TO_MILLIS.apply(dateTimeString).longValue()
    );
  }

  // this function is not used in production, but it is used to create
  // mock objects in unit tests; so its correctness is important
  @Test
  public void testDateStringToMillis() {
    String dateString = "2018-01-02";
    assertEquals(
        LocalDate.of(2018, 1, 2).atStartOfDay(SYSTEM_ZONE).toInstant().toEpochMilli(),
        DATE_STRING_TO_MILLIS.apply(dateString).longValue()
    );
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
