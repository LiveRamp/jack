package com.rapleaf.jack.util;

import org.junit.Test;

import com.rapleaf.jack.JackTestCase;

public abstract class TestQueryRetryPolicy extends JackTestCase {

  public final int RETRIES = 10;

  @Test
  public void testNoRetriesAndNoFailures() {
    testRetriesInternal(0, 0);
  }

  @Test
  public void testNoRetriesOnFailure() {
    testRetriesInternal(RETRIES, 0);
  }

  @Test
  public void testSuccessAtFirstAttempt() {
    testRetriesInternal(0, RETRIES);
  }

  @Test
  public void testFailuresEqualsRetries() {
    testRetriesInternal(RETRIES, RETRIES);
  }

  @Test
  public void testFailuresExceedingMaxRetries() {
    testRetriesInternal(RETRIES + 1, RETRIES);
  }

  @Test
  public void testTwiceTheFailuresAsMaxRetries() {
    testRetriesInternal(RETRIES * 2, RETRIES);
  }

  @Test
  public void testFailuresLowerThanMaxRetries() {
    testRetriesInternal(RETRIES - 1, RETRIES);
  }

  @Test
  public void testHalfTheFailuresAsMaxRetries() {
    testRetriesInternal(RETRIES / 2, RETRIES);
  }

  protected abstract void testRetriesInternal(int numFailures, int maxRetries);
}
