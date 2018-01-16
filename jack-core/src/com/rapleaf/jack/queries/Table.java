package com.rapleaf.jack.queries;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface Table {

  public String getName();

  public String getAlias();

  public Set<Column> getAllColumns();

  public String getSqlKeyword();

  default List<?> getParameters() {
    return Collections.emptyList();
  }

}
