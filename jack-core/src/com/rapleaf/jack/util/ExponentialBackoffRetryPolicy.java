package com.rapleaf.jack.util;

import java.util.Random;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.exception.SqlExecutionFailureException;
import com.rapleaf.jack.transaction.ITransactor;

public class ExponentialBackoffRetryPolicy implements ITransactor.RetryPolicy {

  private static final Logger LOG = LoggerFactory.getLogger(ExponentialBackoffRetryPolicy.class);

  public static final int DEFAULT_MAX_RETRIES = 3;
  public static final double DEFAULT_INTERVAL_MULTIPLIER = 2.0;

  private boolean succeeded = false;
  private int numFailures = 0;

  private int maxRetries;
  private int retryInterval;
  private double multiplier;

  public ExponentialBackoffRetryPolicy() {
    setMaxRetries(DEFAULT_MAX_RETRIES).setMultiplier(DEFAULT_INTERVAL_MULTIPLIER)
        .setRetryInterval(new Random().nextInt(5000) + 10000);
  }

  public ExponentialBackoffRetryPolicy setMaxRetries(int maxRetries) {
    Preconditions.checkArgument(maxRetries > 0, "Number of retries must be greater than 0.");
    this.maxRetries = maxRetries;
    return this;
  }

  public ExponentialBackoffRetryPolicy setRetryInterval(int retryInterval) {
    Preconditions.checkArgument(retryInterval > 0, "Retry interval must be greater than 0 ms.");
    this.retryInterval = retryInterval;
    return this;
  }

  public ExponentialBackoffRetryPolicy setMultiplier(double intervalMultiplier) {
    Preconditions.checkArgument(intervalMultiplier > 0, "Multiplier must be a positive number");
    this.multiplier = intervalMultiplier;
    return this;
  }

  @Override
  public void onFailure(Exception cause) {
    Preconditions.checkState(!succeeded, "Cannot register failure after previous success");
    ++numFailures;
    if (numFailures > maxRetries) {
      throw new SqlExecutionFailureException(cause);
    }
  }

  @Override
  public void onSuccess() {
    succeeded = true;
  }

  @Override
  public boolean execute() {
    /* Do not execute policy if:
       1. Already succeeded OR
       2. No failure has occurred yet OR
       3. Retries have been exhausted
     */
    if (succeeded || numFailures == 0 || numFailures > maxRetries) {
      return false;
    }
    LOG.warn("Retry #" + numFailures + ": Going to sleep for " + retryInterval + " milliseconds.");
    sleep(retryInterval);
    retryInterval *= multiplier;
    return true;
  }

  @VisibleForTesting
  protected void sleep(int duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    }
  }

  public int getMaxRetries() {
    return maxRetries;
  }

  public int getRetryInterval() {
    return retryInterval;
  }

  public double getMultiplier() {
    return multiplier;
  }
}
