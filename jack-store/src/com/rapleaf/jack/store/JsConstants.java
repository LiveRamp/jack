package com.rapleaf.jack.store;

import java.util.Collections;

public class JsConstants {

  public static final String SCOPE_KEY = "_scope_name";

  public static final String ROOT_SCOPE_NAME = "_root_scope";
  public static final JsScope ROOT_SCOPE = new JsScope(null, null, ROOT_SCOPE_NAME);

  public static final JsScopes EMPTY_SCOPES = JsScopes.of(Collections.emptyList());

  public enum DefaultTableField {
    scope, key, type, value
  }

}
