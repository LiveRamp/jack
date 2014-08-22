package com.rapleaf.jack.queries.where_operators;

public class Match extends WhereOperator<String> {

  public Match(String pattern) {
    super(pattern);
    ensureNoNullParameter();
  }

  @Override
  public String getSqlStatement() {
    return "LIKE  ?";
  }

  @Override
  public boolean apply(String value) {
    return value.matches(getSingleParameter().replace("%", "(.*)"));
  }
}
