package com.rapleaf.jack.generic_queries;

public class LimitCondition implements QueryCondition {

  private final int offset;
  private final int limit;

  public LimitCondition(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }

  @Override
  public String getSqlStatement() {
    return "LIMIT " + limit + " OFFSET " + offset;
  }
}
