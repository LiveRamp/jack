package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class NotIn<V> extends QueryOperator<V> {

  public NotIn(V value1, V... otherValues) {
    super(value1, otherValues);
  }

  @Override
  public String getSqlStatement() {
    StringBuilder sb = new StringBuilder(" NOT IN ( ?");
    for (int i = 0; i < getParameters().size() - 1; i++) {
      sb.append(", ?");
    }
    sb.append(" ) ");
    return sb.toString();
  }

  @Override
  public boolean apply(V value) {
    for (V param : getParameters()) {
      if (value != null && value.equals(param)) {
        return false;
      }
      if (value == null && param == null) {
        return false;
      }
    }
    return true;
  }
}
