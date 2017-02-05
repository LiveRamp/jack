package com.rapleaf.jack.exception;

public class TransactionFailureException extends JackRuntimeException {
  public TransactionFailureException(Exception exception) {
    super(exception);
  }

  public TransactionFailureException(String message, Exception exception) {
    super(message, exception);
  }
}
