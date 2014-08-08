package com.rapleaf.jack.sql_operators;

import com.rapleaf.jack.SqlOperator;

public class EqualTo<T> extends SqlOperator<T> {

  public EqualTo(T parameter) {
    super(parameter);
  }

  @Override
  public String getSqlStatement() {
    return getSingleParameter() != null ? " = ? " : " IS NULL";
  }

  @Override
  public int getNbNotNullParameters() {
    return getSingleParameter() != null ? 1 : 0;
  }

  public T getSingleParameter() {
    return getParameters().get(0);
  }
}
