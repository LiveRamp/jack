package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import com.rapleaf.jack.queries.where_operators.IWhereOperator;

public class GenericConstraint<T> implements QueryCondition {
  private final Column<T> column;
  private final IWhereOperator<T> operator;
  private final List<List<GenericConstraint>> chainedOrConstraints;

  GenericConstraint(Column<T> column, IWhereOperator<T> operator) {
    this.column = column;
    this.operator = operator;
    this.chainedOrConstraints = Lists.newArrayList();
  }

  public GenericConstraint<T> or(GenericConstraint constraint, GenericConstraint... otherConstraints) {
    List<GenericConstraint> chainedAndConstraints = Lists.newArrayList(constraint);
    chainedAndConstraints.addAll(Arrays.asList(otherConstraints));
    chainedOrConstraints.add(chainedAndConstraints);
    return this;
  }

  @SuppressWarnings("unchecked")
  public List getParameters() {
    List parameters = Lists.newArrayList();
    parameters.addAll(operator.getParameters());
    for (List<GenericConstraint> orConstraint : chainedOrConstraints) {
      for (GenericConstraint andConstraint : orConstraint) {
        parameters.addAll(andConstraint.getParameters());
      }
    }
    return parameters;
  }

  @Override
  public String getSqlStatement() {
    StringBuilder statement = new StringBuilder("(")
        .append(column.getSqlKeyword()).append(" ")
        .append(operator.getSqlStatement());

    for (List<GenericConstraint> orConstraints : chainedOrConstraints) {
      statement.append(" OR ");

      Iterator<GenericConstraint> it = orConstraints.iterator();
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
