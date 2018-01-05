package com.rapleaf.jack.queries;

import java.util.List;

public class SingleValue extends ValueExpression {
  public SingleValue(String queryStatement, List<Object> parameters) {
    super(queryStatement, parameters);
  }
}
