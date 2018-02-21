package com.rapleaf.jack.util;

public class ExponentialBackoff {
  private static final long INITIAL_BACKOFF_INTERVAL = 500l;

  private final int maxRetries;

  private int numRetries;
  private long backoffMs;

  public ExponentialBackoff(int maxRetries) {
    this(maxRetries, INITIAL_BACKOFF_INTERVAL);
  }

  public ExponentialBackoff(int maxRetries, long backoffMs) {
    this.maxRetries = maxRetries;
    this.numRetries = 0;
    this.backoffMs = backoffMs;
  }

  public void backoff() {
    try {
      Thread.sleep(backoffMs);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    if (numRetries < maxRetries) {
      numRetries++;
      backoffMs <<= 1;
    }
  }

  public boolean isMaxedOut() {
    return numRetries >= maxRetries;
  }
}

