package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class Match extends SqlOperator<String> {

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
