package com.rapleaf.jack.queries.where_operators;

public class NotIn<V> extends WhereOperator<V> {

  public NotIn(V value1, V... otherValues) {
    super(value1, otherValues);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    StringBuilder sb = new StringBuilder("NOT IN (?");
    for (int i = 0; i < getParameters().size() - 1; i++) {
      sb.append(", ?");
    }
    sb.append(")");
    return sb.toString();
  }

  @Override
  public boolean apply(V value) {
    for (V param : getParameters()) {
      if (param.equals(value)) {
        return false;
      }
    }
    return true;
  }
}
