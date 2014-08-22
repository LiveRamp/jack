package com.rapleaf.jack.queries.where_operators;

public class In<V> extends WhereOperator<V> {

  public In(V value1, V... otherValues) {
    super(value1, otherValues);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    StringBuilder sb = new StringBuilder("IN ( ?");
    for (int i = 0; i < getParameters().size() - 1; i++) {
      sb.append(", ?");
    }
    sb.append(" )");
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
