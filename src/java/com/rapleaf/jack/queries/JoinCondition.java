package com.rapleaf.jack.queries;

import com.rapleaf.jack.Column;
import com.rapleaf.jack.Table;

public class JoinCondition implements QueryCondition {
  private final JoinType joinType;
  private final Table table;
  private final Column column1;
  private final Column column2;

  JoinCondition(JoinType joinType, Table table, Column column1, Column column2) {
    this.joinType = joinType;
    this.table = table;
    this.column1 = column1;
    this.column2 = column2;
  }

  Table getTable() {
    return table;
  }

  @Override
  public String getSqlStatement() {
    return joinType.getSqlKeyword() + " " + table.getSqlKeyword() + " ON " + column1.getSqlKeyword() + " = " + column2.getSqlKeyword();
  }
}
