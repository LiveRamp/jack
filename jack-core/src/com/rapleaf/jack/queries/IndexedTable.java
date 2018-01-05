package com.rapleaf.jack.queries;

import java.util.Set;

import com.rapleaf.jack.AttributesWithId;
import com.rapleaf.jack.ModelWithId;

public class IndexedTable<A extends AttributesWithId, M extends ModelWithId> extends AbstractTable<A, M> {

  private final AbstractTable<A, M> table;
  private final Set<IndexHint> indexHints;

  public IndexedTable(AbstractTable<A, M> table, Set<IndexHint> indexHints) {
    super(table);
    this.table = table;
    this.indexHints = indexHints;
  }

  public Set<IndexHint> getIndexHints() {
    return indexHints;
  }

  @Override
  public String getSqlKeyword() {
    return table.getSqlKeyword() + (indexHints.isEmpty() ? "" : AbstractExecution.getClauseFromQueryConditions(indexHints, " ", " ", ""));
  }

}
