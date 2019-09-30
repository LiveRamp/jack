package com.rapleaf.jack.util;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class TestExponentialBackoffPolicy extends TestQueryRetryPolicy {

  @Test(expected = IllegalArgumentException.class)
  public void testNoRetriesAndNoFailures() {
    super.testNoRetriesAndNoFailures();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNoRetriesOnFailure() {
    super.testNoRetriesOnFailure();
  }

  @Override
  protected void testRetriesInternal(int numFailures, int maxRetries) {

    int numRetries = Math.min(numFailures, maxRetries);
    MockRetryPolicy policy = new MockRetryPolicy();
    int duration = numRetries == 0 ? 0 : policy.getRetryInterval();

    policy.setMaxRetries(maxRetries);
    /* Set multiplier randomly in the range [1, 3] */
    policy.setMultiplier(1 + new Random().nextInt(2000) / 2000.0);

    /* Policy should not execute when no failure has occurred (Initial Condition) */
    Assert.assertFalse(policy.shouldRetry());
    policy.execute();
    Assert.assertEquals(0, policy.numSleepCalled);
    Assert.assertEquals(0, policy.lastSleptDuration);

    for (int i = 0; i < numFailures; ++i) {
      policy.updateOnFailure();
      Assert.assertEquals("Retry : " + (i + 1), i < maxRetries, policy.shouldRetry());
      policy.execute();
    }

    /* Cannot use Math.pow() here due to rounding errors */
    for (int i = 0; i < numRetries - 1; ++i) {
      duration *= policy.getMultiplier();
    }

    if (numFailures <= maxRetries) {
      policy.updateOnSuccess();
    }
    Assert.assertEquals(numRetries, policy.numSleepCalled);
    Assert.assertEquals(duration, policy.lastSleptDuration);

    /* Policy should not execute after a successful attempt or when retries have been exhausted (Final Condition) */
    Assert.assertFalse(policy.shouldRetry());
    policy.execute();
    Assert.assertEquals(numRetries, policy.numSleepCalled);
    Assert.assertEquals("Multiplier: " + policy.getMultiplier(), duration, policy.lastSleptDuration);
  }

  private static class MockRetryPolicy extends ExponentialBackoffRetryPolicy {

    int numSleepCalled = 0;
    int lastSleptDuration = 0;

    @Override
    protected void sleep(int duration) {
      ++numSleepCalled;
      lastSleptDuration = duration;
    }
  }
}
