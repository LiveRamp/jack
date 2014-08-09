package com.rapleaf.jack.query_operators;

import com.rapleaf.jack.QueryOperator;

public class Match extends QueryOperator<String> {

  public Match(String pattern) {
    super(pattern);
    if (pattern == null) {
      throw new IllegalArgumentException("You cannot pass null parameters.");
    }
  }

  @Override
  public String getSqlStatement() {
    return " LIKE  ? ";
  }

  @Override
  public boolean apply(String value) {
    return value.matches(getParameter().replace("%", "(.*)"));
  }

  public String getParameter() {
    return getParameters().get(0);
  }
}
