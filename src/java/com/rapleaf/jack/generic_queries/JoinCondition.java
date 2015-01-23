package com.rapleaf.jack.generic_queries;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.ModelWithId;

public class JoinCondition implements QueryCondition {
  private final JoinType joinType;
  private final Class<? extends ModelWithId> model;
  private final ModelField modelField1;
  private final ModelField modelField2;

  public JoinCondition(JoinType joinType, Class<? extends ModelWithId> model, ModelField modelField1, ModelField modelField2) {
    this.joinType = joinType;
    this.model = model;
    this.modelField1 = modelField1;
    this.modelField2 = modelField2;
  }

  public Class<? extends ModelWithId> getModel() {
    return model;
  }

  @Override
  public String getSqlStatement() {
    return joinType.getSqlKeyword() + " " + Utility.getTableName(model) + " ON " + modelField1.getSqlKeyword() + " = " + modelField2.getSqlKeyword();
  }
}
