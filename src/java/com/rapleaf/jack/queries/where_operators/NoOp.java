package com.rapleaf.jack.queries.where_operators;

import java.util.Collections;
import java.util.List;

public class NoOp<T> implements IWhereOperator<T> {
  @Override
  public String getSqlStatement() {
    return "1=1";
  }

  @Override
  public List<T> getParameters() {
    return Collections.emptyList();
  }
}
