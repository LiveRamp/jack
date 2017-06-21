package com.rapleaf.jack.store;

public class JsConstants {

  public static final String SCOPE_KEY = "_scope_name";
  public static final String SCOPE_TYPE = "_SCOPE";
  public static final JsScope ROOT_SCOPE = new JsScope(null, "_root_scope");

  public enum ValueType {
    BOOLEAN(), INT(), LONG(), DOUBLE(), DATETIME(), STRING(), JSON(),
    BOOLEAN_LIST(true), INT_LIST(true), LONG_LIST(true), DOUBLE_LIST(true), DATETIME_LIST(true), STRING_LIST(true);

    private final boolean isList;

    ValueType() {
      this.isList = false;
    }

    ValueType(boolean isList) {
      this.isList = isList;
    }

    public boolean isList() {
      return isList;
    }
  }

}
