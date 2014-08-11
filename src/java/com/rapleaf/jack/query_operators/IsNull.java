package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class IsNull extends QueryOperator {

  public IsNull() {
    super();
  }

  @Override
  public String getSqlStatement() {
    return " IS NULL ";
  }

  @Override
  public boolean apply(Object value) {
    return value == null;
  }
}
