package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class In<T> extends QueryOperator<T> {

  public In(T... parameters) {
    super(parameters);
    for (T parameter : parameters) {
      if (parameter == null) {
        throw new IllegalArgumentException("You cannot pass null parameters.");
      }
    }
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

  @Override
  public boolean apply(T value) {
    for (T t : getParameters()) {
      if (t == value) {
        return true;
      }
    }
    return false;
  }
}
