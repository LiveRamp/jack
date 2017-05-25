package com.rapleaf.jack.queries.where_operators;

import java.util.List;

public interface IWhereOperator<T> {

  public String getSqlStatement();

  public List<T> getParameters();

}
