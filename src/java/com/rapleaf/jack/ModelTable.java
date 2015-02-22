package com.rapleaf.jack;

import java.util.Set;

public interface ModelTable {

  public Set<Column> getAllColumns();

  public String getSqlKeyword();

}
