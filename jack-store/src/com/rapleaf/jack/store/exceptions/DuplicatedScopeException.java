package com.rapleaf.jack.store.exceptions;

import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsScope;

public class DuplicatedScopeException extends JackRuntimeException {
  public DuplicatedScopeException(String scopeName, JsScope parentScope) {
    super(String.format("Duplicated scopes, %s, exist under parent scope %s", scopeName, parentScope.toString()));
  }
}
