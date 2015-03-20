package com.rapleaf.jack;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class AbstractTable implements Table {
  protected final String table;
  protected final String alias;
  protected final Set<Column> allColumns;

  protected AbstractTable(String table, String alias) {
    Preconditions.checkArgument(Strings.isNullOrEmpty(table), "Table name cannot be null or empty.");
    Preconditions.checkArgument(Strings.isNullOrEmpty(alias), "Table alias cannot be null or empty.");
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
