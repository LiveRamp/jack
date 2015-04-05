package com.rapleaf.jack.queries.where_operators;

public class Match extends WhereOperator<String> {

  public Match(String pattern) {
    super("LIKE ?", pattern);
  }
}
