package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;

import com.rapleaf.jack.queries.MultiValue;

public class In<V> extends WhereOperator<V> {

  public In(V value1, V... otherValues) {
    super(null, value1, otherValues);
    this.sqlStatement = createSqlStatement();
  }

  public In(Collection<V> collection) {
    super(null, collection);
    this.sqlStatement = createSqlStatement();
  }

  public In(MultiValue<V> subQuery) {
    super("IN (" + subQuery.getQueryStatement() + ")", (Collection<V>)subQuery.getParameters());
    this.sqlStatement = getSqlStatement();
  }

  private String createSqlStatement() {
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
