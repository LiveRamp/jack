package com.rapleaf.jack.exception;

public class SqlExecutionFailureException extends JackRuntimeException {
  public SqlExecutionFailureException(Exception exception) {
    super(exception);
  }

  public SqlExecutionFailureException(String message, Exception exception) {
    super(message, exception);
  }
}
