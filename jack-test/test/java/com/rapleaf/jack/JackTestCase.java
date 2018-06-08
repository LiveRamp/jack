package com.rapleaf.jack;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;

import com.rapleaf.jack.util.JackUtility;

public class JackTestCase {
  private static final String SEPARATOR = "--------------------";

  protected static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();
  protected static final Function<LocalDateTime, Long> DATETIME_TO_MILLIS = dateTime -> dateTime.atZone(SYSTEM_ZONE).toInstant().toEpochMilli();
  protected static final Function<LocalDate, Long> DATE_TO_MILLIS = date -> date.atStartOfDay(SYSTEM_ZONE).toInstant().toEpochMilli();

  protected static final Function<String, Long> DATE_STRING_TO_MILLIS = date -> DATE_TO_MILLIS.apply(LocalDate.parse(date, JackUtility.DATE_FORMATTER));
  protected static final Function<String, Long> DATETIME_STRING_TO_MILLIS = dateTime -> DATETIME_TO_MILLIS.apply(LocalDateTime.parse(dateTime, JackUtility.TIMESTAMP_FORMATTER));

  public JackTestCase() {
    Logger rootLogger = Logger.getRootLogger();

    rootLogger.setLevel(Level.ALL);

    ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout("%d{yy/MM/dd HH:mm:ss} %p %c{2}: %m%n"), ConsoleAppender.SYSTEM_ERR);
    consoleAppender.setName("test-console-appender");
    consoleAppender.setFollow(true);

    rootLogger.removeAppender("test-console-appender");
    rootLogger.addAppender(consoleAppender);
  }

  @Before
  public final void printSeparators() throws Exception {
    System.out.println(SEPARATOR);
    System.out.println("TEST: " + getName());
    System.out.println(SEPARATOR);
  }

  private String getName() {
    return getClass().getSimpleName();
  }

  protected void sleepSeconds(int seconds) {
    try {
      Thread.sleep(Duration.ofSeconds(seconds).toMillis());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  protected void sleepMillis(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
