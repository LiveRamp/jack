package com.rapleaf.jack.exception;

public class ConnectionCreationFailureException extends JackRuntimeException {
  public ConnectionCreationFailureException(String message, Exception exception) {
    super(message, exception);
  }
}
