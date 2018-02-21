package com.rapleaf.jack.queries;

import java.util.List;

public class SingleValue<T> extends ValueExpression<T> {
  public SingleValue(String queryStatement, List<Object> parameters) {
    super(queryStatement, parameters);
  }

  public <M> SingleValue<M> as(Class<M> type) {
    return new SingleValue<>(queryStatement, parameters);
  }
}
