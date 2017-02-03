package com.rapleaf.jack.exception;

public class ConnectionCreationFailureException extends JackException {
  public ConnectionCreationFailureException(String message) {
    super(message);
  }

  public ConnectionCreationFailureException(String message, Exception exception) {
    super(message, exception);
  }
}
