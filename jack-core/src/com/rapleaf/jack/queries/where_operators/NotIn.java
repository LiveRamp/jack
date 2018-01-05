package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

import com.rapleaf.jack.queries.MultiValue;

public class NotIn<V> extends WhereOperator<V> {

  public NotIn(V value1, V... otherValues) {
    super(null, value1, otherValues);
    this.sqlStatement = createSqlStatement();
  }

  public NotIn(Collection<V> values) {
    super(null, values);
    if (getParameters().isEmpty()) {
      throw new IllegalArgumentException("SQL does not accept an empty list as a parameter of NOT IN().");
    }
    this.sqlStatement = createSqlStatement();
  }

  public NotIn(MultiValue subQuery) {
    super("NOT IN (" + subQuery.getQueryStatement() + ")", (Collection<V>)subQuery.getParameters());
  }

  private String createSqlStatement() {
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
