package com.rapleaf.jack.queries;

import java.util.List;

public class MultiValue<T> extends ValueExpression<T> {
  public MultiValue(String queryStatement, List<Object> parameters) {
    super(queryStatement, parameters);
  }

  public <M> MultiValue<M> as(Class<M> type) {
    return new MultiValue<>(queryStatement, parameters);
  }
}
