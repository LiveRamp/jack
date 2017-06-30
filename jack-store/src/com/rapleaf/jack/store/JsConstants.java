package com.rapleaf.jack.store;

import java.util.Collections;

public class JsConstants {

  public static final String SCOPE_KEY = "_scope_name";

  public static final JsScope ROOT_SCOPE = new JsScope(null, "_root_scope");
  public static final JsScopes EMPTY_SCOPES = JsScopes.of(Collections.emptyList());

  public static final JsRecord EMPTY_RECORD = new JsRecord(Collections.emptyMap(), Collections.emptyMap());
  public static final JsRecords EMPTY_RECORDS = new JsRecords(Collections.emptyList());

  public enum DefaultTableField {
    scope, key, type, value
  }

}
