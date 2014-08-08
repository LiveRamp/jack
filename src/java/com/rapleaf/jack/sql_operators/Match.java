package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class Match extends SqlOperator<String> {

  public Match(String pattern) {
    super(pattern);
  }

  @Override
  public String getSqlStatement() {
    return " LIKE  ? ";
  }
}
