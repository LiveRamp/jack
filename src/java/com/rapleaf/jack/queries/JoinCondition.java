package com.rapleaf.jack.queries;

import com.rapleaf.jack.Column;
import com.rapleaf.jack.ModelTable;

public class JoinCondition implements IQueryCondition {
  private final JoinType joinType;
  private final ModelTable table;
  private final Column column1;
  private final Column column2;

  JoinCondition(JoinType joinType, ModelTable table, Column column1, Column column2) {
    this.joinType = joinType;
    this.table = table;
    this.column1 = column1;
    this.column2 = column2;
  }

  ModelTable getTable() {
    return table;
  }

  @Override
  public String getSqlStatement() {
    return joinType.getSqlKeyword() + " " + table.getSqlKeyword() + " ON " + column1.getSqlKeyword() + " = " + column2.getSqlKeyword();
  }
}
