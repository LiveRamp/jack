package com.rapleaf.jack.queries;

import java.util.Set;

import com.rapleaf.jack.queries.Column;

public interface Table {

  public Set<Column> getAllColumns();

  public String getSqlKeyword();

}
