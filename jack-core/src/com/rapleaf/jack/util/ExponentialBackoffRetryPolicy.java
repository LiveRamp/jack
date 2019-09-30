package com.rapleaf.jack.util;

import java.util.Random;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  public boolean shouldRetry() {

    if (numFailures == 0) {
      return false;
    }
    return (!succeeded && numFailures <= maxRetries);
  }

  @Override
  public void updateOnFailure() {
    ++numFailures;
  }

  @Override
  public void updateOnSuccess() {
    succeeded = true;
  }

  @Override
  public void execute() {
    if (shouldRetry()) {
      LOG.warn("Retry #" + numFailures + ": Going to sleep for " + retryInterval + " milliseconds.");
      sleep(retryInterval);
      retryInterval *= multiplier;
    }
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
