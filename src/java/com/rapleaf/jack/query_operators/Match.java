package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class Match extends QueryOperator<String> {

  public Match(String pattern) {
    super(pattern);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    return " LIKE  ? ";
  }

  @Override
  public boolean apply(String value) {
    return value.matches(getSingleParameter().replace("%", "(.*)"));
  }
}
