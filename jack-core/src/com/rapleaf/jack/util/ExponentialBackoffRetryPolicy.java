package com.rapleaf.jack.util;

import java.util.Random;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapleaf.jack.transaction.ITransactor;

public class ExponentialBackoffRetryPolicy implements ITransactor.RetryPolicy {

  private static final Logger LOG = LoggerFactory.getLogger(ExponentialBackoffRetryPolicy.class);

  private int maxRetries = 0;
  private int numFailures = 0;
  private int retryInterval = 0;
  private boolean succeeded = false;

  public ExponentialBackoffRetryPolicy() {
  }

  public ExponentialBackoffRetryPolicy(int maxRetries) {
    this(maxRetries, new Random().nextInt(5000) + 10000);
  }

  public ExponentialBackoffRetryPolicy(int maxRetries, int retryIntervalMs) {
    Preconditions.checkArgument(retryIntervalMs > 0, "Retry interval must be greater than 0 ms.");
    Preconditions.checkArgument(maxRetries > 0, "Number of retries must be greater than 0.");
    this.maxRetries = maxRetries;
    this.retryInterval = retryIntervalMs;
  }

  @Override
  public boolean shouldRetry() {
    return (!succeeded && numFailures > 0 && numFailures <= maxRetries);
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
      retryInterval <<= 1;
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

  protected int getMaxRetries() {
    return maxRetries;
  }

  protected int getRetryInterval() {
    return retryInterval;
  }
}
