package com.rapleaf.jack;

import java.util.Set;

import com.google.common.collect.Sets;

public class AbstractModelTable implements ModelTable {
  protected final String table;
  protected final String alias;
  protected final Set<Column> allColumns;

  protected AbstractModelTable(String table, String alias) {
    this.table = table;
    this.alias = alias;
    this.allColumns = Sets.newHashSet();
  }

  @Override
  public Set<Column> getAllColumns() {
    return allColumns;
  }

  @Override
  public String getSqlKeyword() {
    return table + (alias != null ? " AS " + alias : "");
  }
}
