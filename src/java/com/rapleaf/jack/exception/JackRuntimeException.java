package com.rapleaf.jack.exception;

public class JackRuntimeException extends RuntimeException {
  public JackRuntimeException() {
    super();
  }

  public JackRuntimeException(String message) {
    super(message);
  }

  public JackRuntimeException(String message, Exception exception) {
    super(message, exception);
  }
}
