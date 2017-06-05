package com.rapleaf.jack.queries;

import java.util.Collections;
import java.util.Set;

public class SingleTableReference implements TableReference {

  private final Table table;
  private final Set<IndexHint> indexHints;

  SingleTableReference(Table table) {
    this.table = table;
    this.indexHints = Collections.emptySet();
  }

  SingleTableReference(Table table, Set<IndexHint> indexHints) {
    this.table = table;
    this.indexHints = indexHints;
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
    return table.getSqlKeyword() + (indexHints.isEmpty() ? "" : AbstractExecution.getClauseFromQueryConditions(indexHints, " ", " ", ""));
  }

}
