package com.rapleaf.jack.queries.where_operators;

import java.util.List;

public class GenericOperator<V> extends WhereOperator<V> {

  public GenericOperator(String sqlStatement, V parameter) {
    super(sqlStatement, parameter);
  }

  public GenericOperator(String sqlStatement, V param1, V param2) {
    super(sqlStatement, param1, param2);
  }

  public GenericOperator(String sqlStatement, List<V> parameters) {
    super(sqlStatement, parameters);
  }

}
