package com.rapleaf.jack.store.exceptions;

import com.rapleaf.jack.exception.JackRuntimeException;

public class MissingScopeException extends JackRuntimeException {
  public MissingScopeException(String scopeName) {
    super(String.format("Scope %s does not exist", scopeName));
  }
}
