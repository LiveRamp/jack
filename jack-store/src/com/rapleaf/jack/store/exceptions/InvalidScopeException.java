package com.rapleaf.jack.store.exceptions;

import com.rapleaf.jack.exception.JackRuntimeException;

public class InvalidScopeException extends JackRuntimeException {
  public InvalidScopeException(String message) {
    super(message);
  }
}
