package com.rapleaf.jack.store;

import java.util.Collections;

public class JsConstants {

  public static final String SCOPE_KEY = "_scope_name";

  public static final String ROOT_SCOPE_NAME = "_root_scope";
  public static final Long ROOT_SCOPE_ID = null;
  public static final JsScope ROOT_SCOPE = new JsScope(ROOT_SCOPE_ID, ROOT_SCOPE_NAME);

  public static final JsScopes EMPTY_SCOPES = JsScopes.of(Collections.emptyList());
  public static final long EMPTY_RECORD_SCOPE_ID = -1L;
  public static final JsRecord EMPTY_RECORD = new JsRecord(EMPTY_RECORD_SCOPE_ID, Collections.emptyMap(), Collections.emptyMap());
  public static final JsRecords EMPTY_RECORDS = new JsRecords(Collections.emptyList());

  public enum DefaultTableField {
    scope, key, type, value
  }

}
