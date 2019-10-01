package com.rapleaf.jack.util;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.rapleaf.jack.exception.SqlExecutionFailureException;

public class TestExponentialBackoffPolicy extends TestQueryRetryPolicy {

  @Override
  @Test(expected = IllegalArgumentException.class)
  public void testNoRetriesAndNoFailures() {
    super.testNoRetriesAndNoFailures();
  }

  @Override
  @Test(expected = IllegalArgumentException.class)
  public void testNoRetriesOnFailure() {
    super.testNoRetriesOnFailure();
  }

  @Test(expected = IllegalStateException.class)
  public void testRegisterFailureAfterSuccess() {
    MockRetryPolicy policy = new MockRetryPolicy();
    policy.onSuccess();
    policy.onFailure(new Exception("Query Failed"));
  }

  @Override
  protected void testRetriesInternal(int numFailures, int maxRetries) {

    Exception exception = new Exception("Query Failed");
    int numRetries = Math.min(numFailures, maxRetries);
    MockRetryPolicy policy = new MockRetryPolicy();
    int duration = numRetries == 0 ? 0 : policy.getRetryInterval();

    policy.setMaxRetries(maxRetries);
    /* Set multiplier randomly in the range [1, 3] */
    policy.setMultiplier(1 + new Random().nextInt(2000) / 2000.0);
    /* Cannot use Math.pow() here due to rounding errors */
    for (int i = 0; i < numRetries - 1; ++i) {
      duration *= policy.getMultiplier();
    }

    /* Policy should not execute when no failure has occurred (Initial Condition) */
    Assert.assertFalse(policy.execute());
    Assert.assertEquals(0, policy.numSleepCalled);
    Assert.assertEquals(0, policy.lastSleptDuration);

    try {
      for (int i = 0; i < numFailures; ++i) {
        policy.onFailure(exception);
        Assert.assertTrue(policy.execute());
      }
      policy.onSuccess();
    } catch (SqlExecutionFailureException se) {
      Assert.assertEquals(se.getCause(), exception);
      Assert.assertEquals(numRetries + 1, policy.numExecuteCalled);
    }
    Assert.assertEquals(numRetries, policy.numSleepCalled);
    Assert.assertEquals(duration, policy.lastSleptDuration);

    /* Policy should not execute after a successful attempt or when retries have been exhausted (Final Condition) */
    Assert.assertFalse(policy.execute());
    Assert.assertEquals(numRetries, policy.numSleepCalled);
    Assert.assertEquals(duration, policy.lastSleptDuration);
  }

  private static class MockRetryPolicy extends ExponentialBackoffRetryPolicy {

    int numExecuteCalled = 0;
    int numSleepCalled = 0;
    int lastSleptDuration = 0;

    @Override
    protected void sleep(int duration) {
      ++numSleepCalled;
      lastSleptDuration = duration;
    }

    @Override
    public boolean execute() {
      ++numExecuteCalled;
      return super.execute();
    }
  }
}
