package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class In<T> extends SqlOperator<T> {

  public In(T... parameters) {
    super(parameters);
  }

  @Override
  public String getSqlStatement() {
    StringBuilder sb = new StringBuilder(" IN ( ?");
    for (int i = 0; i < getParameters().size() - 1; i++) {
      sb.append(", ?");
    }
    sb.append(" ) ");
    return sb.toString();
  }
}
