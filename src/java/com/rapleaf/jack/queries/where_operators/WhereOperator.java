package com.rapleaf.jack.queries.where_operators;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

public abstract class WhereOperator<V> implements IWhereOperator<V> {

  protected final List<V> parameters = Lists.newArrayList();
  protected String sqlStatement;

  protected WhereOperator(String sqlStatement) {
    this.sqlStatement = sqlStatement;
  }

  protected WhereOperator(String sqlStatement, V... parameters) {
    this.sqlStatement = sqlStatement;
    Collections.addAll(this.parameters, parameters);
    ensureNoNullParameter();
  }

  protected WhereOperator(String sqlStatement, Collection<V> parameters) {
    this.sqlStatement = sqlStatement;
    this.parameters.addAll(parameters);
    ensureNoNullParameter();
  }

  @Override
  public List<V> getParameters() {
    return parameters;
  }

  @Override
  public String getSqlStatement() {
    return sqlStatement;
  }

  void ensureNoNullParameter() {
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
