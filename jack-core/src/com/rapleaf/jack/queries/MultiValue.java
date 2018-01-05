package com.rapleaf.jack.queries;

import java.util.List;

public class MultiValue extends ValueExpression {
  public MultiValue(String queryStatement, List<Object> parameters) {
    super(queryStatement, parameters);
  }
}
