package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class EqualTo<V> extends QueryOperator<V> {

  public EqualTo(V value) {
    super(value);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    return "= ?";
  }

  @Override
  public boolean apply(V value) {
    return getSingleParameter().equals(value);
  }
}
