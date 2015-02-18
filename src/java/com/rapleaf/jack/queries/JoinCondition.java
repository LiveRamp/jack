package com.rapleaf.jack.queries;

import com.google.common.base.Optional;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.ModelWithId;

public class JoinCondition implements IQueryCondition {
  private final JoinType joinType;
  private final Class<? extends ModelWithId> model;
  private final Optional<String> modelAlias;
  private final ModelField modelField1;
  private final ModelField modelField2;

  JoinCondition(JoinType joinType, Class<? extends ModelWithId> model, Optional<String> modelAlias, ModelField modelField1, ModelField modelField2) {
    this.joinType = joinType;
    this.model = model;
    this.modelAlias = modelAlias;
    this.modelField1 = modelField1;
    this.modelField2 = modelField2;
  }

  @Override
  public String getSqlStatement() {
    String tableName = Utility.getTableName(model);
    return joinType.getSqlKeyword() + " " + tableName + (modelAlias.isPresent() ? " AS " + modelAlias.get() : "") +
        " ON " + modelField1.getSqlKeyword() + " = " + modelField2.getSqlKeyword();
  }
}
