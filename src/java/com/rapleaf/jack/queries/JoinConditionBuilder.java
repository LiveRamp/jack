package com.rapleaf.jack.queries;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import com.rapleaf.jack.Column;
import com.rapleaf.jack.ModelTable;
import com.rapleaf.jack.ModelWithId;

public class JoinConditionBuilder {
  private final GenericQueryBuilder queryBuilder;
  private final JoinType joinType;
  private final ModelTable table;

  JoinConditionBuilder(GenericQueryBuilder queryBuilder, JoinType joinType, ModelTable table) {
    this.queryBuilder = queryBuilder;
    this.joinType = joinType;
    this.table = table;
  }

  public GenericQueryBuilder on(Column column1, Column column2) {
    queryBuilder.addJoinCondition(new JoinCondition(joinType, table, column1, column2));
    return queryBuilder;
  }
}
