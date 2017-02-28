package com.rapleaf.jack.exception;

public class SqlExecutionFailureException extends JackRuntimeException {
  public SqlExecutionFailureException(Exception exception) {
    super(exception);
  }
}
