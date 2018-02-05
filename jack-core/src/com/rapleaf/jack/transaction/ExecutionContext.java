package com.rapleaf.jack.transaction;

import com.google.common.base.Preconditions;

public class ExecutionContext {
  public static final int DEFAULT_RETRY_TIMES = 1;
  public static final boolean DEFAULT_AS_TRANSACTION = false;

  private final int retryTimes;
  private final boolean asTransaction;

  ExecutionContext(int retryTimes, boolean asTransaction) {
    this.retryTimes = retryTimes;
    this.asTransaction = asTransaction;
  }

  public int getRetryTimes() {
    return retryTimes;
  }

  public boolean isAsTransaction() {
    return asTransaction;
  }

  public static class Builder {
    private int retryTimes;
    private boolean asTransaction;

    public Builder() {
      reset();
    }

    public void reset() {
      retryTimes = DEFAULT_RETRY_TIMES;
      asTransaction = DEFAULT_AS_TRANSACTION;
    }

    public void setRetryTimes(int retryTimes) {
      Preconditions.checkArgument(retryTimes > 0, "Retry times must be greater than 0.");
      this.retryTimes = retryTimes;
    }

    public void setAsTransaction(boolean asTransaction) {
      this.asTransaction = asTransaction;
    }

    public ExecutionContext build() {
      return new ExecutionContext(retryTimes, asTransaction);
    }
  }
}
