package com.rapleaf.jack.queries;

import java.util.List;

public class JoinCondition implements QueryCondition {
  private final JoinType joinType;
  private final TableReference tableReference;
  private final List<GenericConstraint> constraints;

  JoinCondition(JoinType joinType, TableReference tableReference, List<GenericConstraint> constraints) {
    this.joinType = joinType;
    this.tableReference = tableReference;
    this.constraints = constraints;
  }

  TableReference getTableReference() {
    return tableReference;
  }

  @Override
  public String getSqlStatement() {
    return joinType.getSqlKeyword() + " " + tableReference.getSqlStatement() + " ON " + GenericQuery.getClauseFromQueryConditions(constraints, "", " AND ", "");
  }
}
