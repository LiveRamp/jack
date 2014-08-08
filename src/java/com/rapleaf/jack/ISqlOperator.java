package com.rapleaf.jack;

import java.util.List;

public interface ISqlOperator<T> {

  public String getSqlStatement();

  public List<T> getParameters();

  public boolean apply(T value);

  public int getNbNotNullParameters();

}
