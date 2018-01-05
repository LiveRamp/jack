package com.rapleaf.jack.queries;

import java.util.List;

public abstract class ValueExpression {
  private final String queryStatement;
  private final List<Object> parameters;

  ValueExpression(String queryStatement, List<Object> parameters) {
    this.queryStatement = queryStatement;
    this.parameters = parameters;
  }

  public String getQueryStatement() {
    return queryStatement;
  }

  public List<Object> getParameters() {
    return parameters;
  }
}
