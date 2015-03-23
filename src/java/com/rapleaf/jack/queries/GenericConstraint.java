package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class GenericConstraint<T> implements QueryCondition {
  private final Column column;
  private final IWhereOperator<T> operator;
  private final List<List<GenericConstraint>> orConstraints;

  GenericConstraint(Column column, IWhereOperator<T> operator) {
    this.column = column;
    this.operator = operator;
    this.orConstraints = Lists.newArrayList();
  }

  public GenericConstraint or(GenericConstraint constraint, GenericConstraint... constraints) {
    List<GenericConstraint> andConstraints = Lists.newArrayList(constraint);
    andConstraints.addAll(Arrays.asList(constraints));
    this.orConstraints.add(andConstraints);
    return this;
  }

  @Override
  public String getSqlStatement() {
    StringBuilder statement = new StringBuilder("(")
        .append(column.getSqlKeyword()).append(" ")
        .append(operator.getSqlStatement());

    for (List<GenericConstraint> orConstraint : orConstraints) {
      statement.append(" OR ");

      Iterator<GenericConstraint> it = orConstraint.iterator();
      while (it.hasNext()) {
        statement.append(it.next().getSqlStatement());
        if (it.hasNext()) {
          statement.append(" AND ");
        }
      }
    }

    return statement.append(")").toString();
  }
}
