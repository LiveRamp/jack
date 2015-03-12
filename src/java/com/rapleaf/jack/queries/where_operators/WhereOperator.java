package com.rapleaf.jack.queries.where_operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class WhereOperator<V> implements IWhereOperator<V> {

  private final List<V> parameters;

  protected WhereOperator() {
    parameters = Collections.emptyList();
  }

  public WhereOperator(V parameter) {
    this.parameters = new ArrayList<V>();
    this.parameters.add(parameter);
  }

  public WhereOperator(V param1, V param2) {
    this.parameters = new ArrayList<V>();
    this.parameters.add(param1);
    this.parameters.add(param2);
  }

  public WhereOperator(V param1, V... otherParam) {
    this.parameters = new ArrayList<V>();
    this.parameters.add(param1);
    Collections.addAll(this.parameters, otherParam);
  }

  public WhereOperator(Collection<V> collection) {
    this.parameters = new ArrayList<V>(collection);
  }

  public List<V> getParameters() {
    return parameters;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    WhereOperator that = (WhereOperator)o;

    return parameters.equals(that.parameters);
  }

  @Override
  public int hashCode() {
    return parameters.hashCode();
  }
}
