package com.rapleaf.jack.util;

import org.junit.Assert;

public class TestExponentialBackoffPolicy extends TestQueryRetryPolicy {

  @Override
  protected void testRetriesInternal(int numFailures, int maxRetries) {
    int numRetries = Math.min(numFailures, maxRetries);
    MockRetryPolicy policy = maxRetries == 0 ? new MockRetryPolicy() : new MockRetryPolicy(maxRetries);
    int duration = numRetries == 0 ? 0 : (policy.getRetryInterval() << (numRetries - 1));

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
    if (numFailures <= maxRetries) {
      policy.updateOnSuccess();
    }
    Assert.assertEquals(numRetries, policy.numSleepCalled);
    Assert.assertEquals(duration, policy.lastSleptDuration);

    /* Policy should not execute after a successful attempt or when retries have been exhausted (Final Condition) */
    Assert.assertFalse(policy.shouldRetry());
    policy.execute();
    Assert.assertEquals(numRetries, policy.numSleepCalled);
    Assert.assertEquals(duration, policy.lastSleptDuration);
  }

  private static class MockRetryPolicy extends ExponentialBackoffRetryPolicy {

    int numSleepCalled = 0;
    int lastSleptDuration = 0;

    MockRetryPolicy() {
      super();
    }

    MockRetryPolicy(int maxRetries) {
      super(maxRetries);
    }

    @Override
    protected void sleep(int duration) {
      ++numSleepCalled;
      lastSleptDuration = duration;
    }
  }
}
