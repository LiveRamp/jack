package com.rapleaf.jack.queries;

import com.google.common.base.Optional;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.ModelWithId;

public class JoinCondition implements IQueryCondition {
  private final JoinType joinType;
  private final Class<? extends ModelWithId> model;
  private final ModelField modelField1;
  private final ModelField modelField2;
  private final Optional<String> alias;

  JoinCondition(JoinType joinType, Class<? extends ModelWithId> model, Optional<String> alias, ModelField modelField1, ModelField modelField2) {
    this.joinType = joinType;
    this.model = model;
    this.alias = alias;
    this.modelField1 = modelField1;
    this.modelField2 = modelField2;
  }

  @Override
  public String getSqlStatement() {
    String tableName = Utility.getTableName(model);
    return joinType.getSqlKeyword() + " " + tableName + (alias.isPresent() ? " AS " + alias.get() : "") +
        " ON " + modelField1.getSqlKeyword() + " = " + modelField2.getSqlKeyword();
  }
}
