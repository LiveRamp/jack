package com.rapleaf.jack.exception;

public class JackException extends Exception {
  public JackException() {
    super();
  }

  public JackException(String message) {
    super(message);
  }

  public JackException(String message, Exception exception) {
    super(message, exception);
  }
}
