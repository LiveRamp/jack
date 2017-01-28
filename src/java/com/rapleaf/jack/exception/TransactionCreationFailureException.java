package com.rapleaf.jack.exception;

public class TransactionCreationFailureException extends JackException {
  public TransactionCreationFailureException(String message) {
    super(message);
  }

  public TransactionCreationFailureException(String message, Exception exception) {
    super(message, exception);
  }
}
