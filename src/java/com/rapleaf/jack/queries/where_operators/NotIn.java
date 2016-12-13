package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

public class NotIn<V> extends WhereOperator<V> {

  public NotIn(V value, V... otherValues) {
    super(null, value, otherValues);
  }

  public NotIn(Collection<V> values) {
    super(null, values);
    if (getParameters().isEmpty()) {
      throw new IllegalArgumentException("SQL does not accept an empty list as a parameter of NOT IN().");
    }
  }

  @Override
  public String getSqlStatement() {
    StringBuilder sb = new StringBuilder("NOT IN (");

    for (int i = 0; i < getParameters().size(); i++) {
      sb.append("?");
      if (i < getParameters().size() - 1) {
        sb.append(", ");
      }
    }
    sb.append(")");
    return sb.toString();
  }
}
