package com.rapleaf.jack;

import java.util.List;

public interface IQueryOperator<T> {

  public String getSqlStatement();

  public List<T> getParameters();

  public boolean apply(T value);
}
