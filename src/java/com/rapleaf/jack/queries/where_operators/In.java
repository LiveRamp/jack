package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

public class In<V> extends WhereOperator<V> {

  public In(V value1, V... otherValues) {
    super(value1, otherValues);
    ensureNoNullParameter();
  }

  public In(Collection<V> collection) {
    super(collection);
    ensureNoNullParameter();
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

  @Override
  public boolean apply(V value) {
    for (V param : getParameters()) {
      if (param.equals(value)) {
        return true;
      }
    }
    return false;
  }
}
