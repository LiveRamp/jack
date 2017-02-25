package com.rapleaf.jack.exception;

public class ConnectionClosureFailureException extends JackRuntimeException {
  public ConnectionClosureFailureException(String message, Exception exception) {
    super(message, exception);
  }
}
