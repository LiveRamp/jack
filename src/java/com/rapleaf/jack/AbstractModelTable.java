package com.rapleaf.jack;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public class AbstractModelTable implements ModelTable {
  protected final String table;
  protected final String alias;
  protected final Set<Column> allColumns;

  protected AbstractModelTable(String table, String alias) {
    Preconditions.checkArgument(table != null && !table.isEmpty(), "Table name cannot be null or empty.");
    Preconditions.checkArgument(alias != null && !alias.isEmpty(), "Table alias cannot be null or empty.");
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
    return table + " AS " + alias;
  }
}
