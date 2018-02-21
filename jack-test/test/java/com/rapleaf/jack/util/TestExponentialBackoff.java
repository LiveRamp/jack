package com.rapleaf.jack.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestExponentialBackoff {

  @Test
  public void testZeroRetriesIsAlwaysMaxedOut() {
    ExponentialBackoff eb = new ExponentialBackoff(0);
    assertTrue(eb.isMaxedOut());
  }

  @Test
  public void testManyRetries() {
    int expectedRetries = 3;
    int actualRetries = 0;

    ExponentialBackoff eb = new ExponentialBackoff(expectedRetries);
    while (!eb.isMaxedOut()) {
      eb.backoff();
      actualRetries++;
    }

    assertEquals(expectedRetries, actualRetries);
  }

  @Test
  public void testBackoffIsExponential() {
    int retries = 3;
    long initialBackoffTimeMs = 100l;
    long expectedWaitTime = computeExpectedWaitTime(retries, initialBackoffTimeMs);

    long startTime = System.currentTimeMillis();
    ExponentialBackoff eb = new ExponentialBackoff(retries, initialBackoffTimeMs);
    while (!eb.isMaxedOut()) {
      eb.backoff();
    }

    long actualWaitTime = System.currentTimeMillis() - startTime;

    assertTrue(actualWaitTime >= expectedWaitTime);
  }

  private long computeExpectedWaitTime(int retries, long backoffTimeMs) {
    long expectedWaitTime = 0;

    for (int i = 0; i < retries; i++) {
      expectedWaitTime += backoffTimeMs << i;
    }

    return expectedWaitTime;
  }
}
