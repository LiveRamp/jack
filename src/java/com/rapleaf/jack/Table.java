package com.rapleaf.jack;

import java.util.Set;

public interface Table {

  public Set<Column> getAllColumns();

  public String getSqlKeyword();

}
