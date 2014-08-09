package com.rapleaf.jack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class QueryOperator<T> implements IQueryOperator<T> {

  private final List<T> parameters;

  public QueryOperator(T parameter) {
    this.parameters = new ArrayList<T>();
    this.parameters.add(parameter);
  }

  public QueryOperator(T param1, T param2) {
    this.parameters = new ArrayList<T>();
    this.parameters.add(param1);
    this.parameters.add(param2);
  }

  public QueryOperator(T... parameters) {
    this.parameters = Arrays.asList(parameters);
  }

  public List<T> getParameters() {
    return parameters;
  }
}
