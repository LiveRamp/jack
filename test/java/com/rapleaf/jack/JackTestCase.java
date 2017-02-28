package com.rapleaf.jack;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;

public class JackTestCase {

  private static final String SEPARATOR = "--------------------";

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

}
