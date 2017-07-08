package com.rapleaf.jack.store.exceptions;

import com.rapleaf.jack.exception.JackRuntimeException;

public class InvalidRecordException extends JackRuntimeException {
  public InvalidRecordException(String message) {
    super(message);
  }
}
