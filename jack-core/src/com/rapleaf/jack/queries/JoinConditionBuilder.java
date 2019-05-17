package com.rapleaf.jack.queries;

import java.util.List;

import com.google.common.collect.Lists;

public class JoinConditionBuilder<E extends JoinableExecution> {
  private final E genericQuery;
  private final JoinType joinType;
  private final Table table;

  JoinConditionBuilder(E genericQuery, JoinType joinType, Table table) {
    this.genericQuery = genericQuery;
    this.joinType = joinType;
    this.table = table;
  }

  public E on(GenericConstraint constraint, GenericConstraint... constraints) {
    List<GenericConstraint> joinConstraints = Lists.newArrayList(constraint);
    genericQuery.addParameters(constraint.getParameters());
    for (GenericConstraint genericConstraint : constraints) {
      joinConstraints.add(genericConstraint);
      genericQuery.addParameters(genericConstraint.getParameters());
    }
    genericQuery.addJoinCondition(new JoinCondition(joinType, table, joinConstraints));
    return genericQuery;
  }
}
