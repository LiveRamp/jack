package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

public class In<V> extends WhereOperator<V> {

  public In(V value1, V... otherValues) {
    super(null, value1, otherValues);
  }

  public In(Collection<V> collection) {
    super(null, collection);
  }

  @Override
  public String getSqlStatement() {

    StringBuilder sb = new StringBuilder("IN (");

    if (getParameters().isEmpty()) {
      sb.append("null");
    } else {
      for (int i = 0; i < getParameters().size(); i++) {
        sb.append("?");
        if (i < getParameters().size() - 1) {
          sb.append(", ");

        }
      }
    }
    sb.append(")");
    return sb.toString();
  }
}
