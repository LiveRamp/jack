package com.rapleaf.jack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class QueryOperator<V> implements IQueryOperator<V> {

  private final List<V> parameters;

  protected QueryOperator() {
    parameters = Collections.emptyList();
  }

  public QueryOperator(V parameter) {
    this.parameters = new ArrayList<V>();
    this.parameters.add(parameter);
  }

  public QueryOperator(V param1, V param2) {
    this.parameters = new ArrayList<V>();
    this.parameters.add(param1);
    this.parameters.add(param2);
  }

  public QueryOperator(V param1, V... otherParam) {
    this.parameters = new ArrayList<V>();
    this.parameters.add(param1);
    Collections.addAll(this.parameters, otherParam);
  }

  public List<V> getParameters() {
    return parameters;
  }

  /*
  Return the first parameter.
  Useful for operators with only one parameter.
   */
  public V getSingleParameter() {
    return parameters.get(0);
  }

  /*
  Check that none of the parameters is null.
  Throw an IllegalArgumentException if a null parameter is found.
   */
  public void ensureNoNullParameter() {
    for (V parameter : parameters) {
      if (parameter == null) {
        throw new IllegalArgumentException("You cannot pass null parameters.");
      }
    }
  }
}
