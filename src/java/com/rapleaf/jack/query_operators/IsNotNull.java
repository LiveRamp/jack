package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class IsNotNull extends QueryOperator {

  public IsNotNull() {
    super();
  }

  @Override
  public String getSqlStatement() {
    return " IS NOT NULL ";
  }

  @Override
  public boolean apply(Object value) {
    return value != null;
  }
}
