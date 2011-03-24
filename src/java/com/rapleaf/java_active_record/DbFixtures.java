package com.rapleaf.java_active_record;

public class DbFixtures {
  protected static String getTruncateTableStmt(String tableName) {
    return "truncate table " + tableName;
  }
}
