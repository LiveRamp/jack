package com.rapleaf.jack.queries;

import java.util.List;

public class JoinCondition implements QueryCondition {
  private final JoinType joinType;
  private final Table table;
  private final List<GenericConstraint> constraints;

  JoinCondition(JoinType joinType, Table table, List<GenericConstraint> constraints) {
    this.joinType = joinType;
    this.table = table;
    this.constraints = constraints;
  }

  Table getTable() {
    return table;
  }

  @Override
  public String getSqlStatement() {
    return joinType.getSqlKeyword() + " " + table.getSqlKeyword() + " ON " + GenericQuery.getClauseFromQueryConditions(constraints, "", " AND ", "");
  }
}
