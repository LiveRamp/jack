package com.rapleaf.jack.queries.where_operators;

import java.util.List;

public interface IWhereOperator<T> {
  String getSqlStatement();

  List<T> getParameters();
}
