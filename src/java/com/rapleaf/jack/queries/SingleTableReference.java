package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

public class SingleTableReference implements TableReference {

  private final Table table;
  private final Set<IndexHint> indexHints;

  SingleTableReference(final Table table) {
    this.table = table;
    this.indexHints = Collections.emptySet();
  }

  SingleTableReference(final Table table, final IndexHint indexHint, final IndexHint... indexHints) {
    this.table = table;
    this.indexHints = Sets.newHashSet(indexHint);
    this.indexHints.addAll(Arrays.asList(indexHints));
  }

  @Override
  public Table getTable() {
    return table;
  }

  @Override
  public Set<IndexHint> getIndexHints() {
    return indexHints;
  }

  @Override
  public String getSqlStatement() {
    return table.getSqlKeyword() + (indexHints.isEmpty() ? "" : GenericQuery.getClauseFromQueryConditions(indexHints, " ", " ", ""));
  }
}
