package com.rapleaf.jack.queries;

import java.util.Set;

public interface TableReference extends QueryCondition {

  Table getTable();

  Set<IndexHint> getIndexHints();

}
