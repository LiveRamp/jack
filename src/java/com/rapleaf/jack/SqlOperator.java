package com.rapleaf.jack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SqlOperator<T> implements ISqlOperator<T> {

  private final List<T> parameters;

  public SqlOperator(T parameter) {
    this.parameters = new ArrayList<T>();
    this.parameters.add(parameter);
  }

  public SqlOperator(T param1, T param2) {
    this.parameters = new ArrayList<T>();
    this.parameters.add(param1);
    this.parameters.add(param2);
  }

  public SqlOperator(T... parameters) {
    this.parameters = Arrays.asList(parameters);
  }

  public List<T> getParameters() {
    return parameters;
  }
}
