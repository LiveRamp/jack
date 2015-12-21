package com.rapleaf.jack.queries.where_operators;

import java.util.Collections;
import java.util.List;

public class NoOp<T> implements IWhereOperator<T> {
  @Override
  public String getSqlStatement() {
    return "is not null OR true";
  }

  @Override
  public List<T> getParameters() {
    return Collections.emptyList();
  }
}
