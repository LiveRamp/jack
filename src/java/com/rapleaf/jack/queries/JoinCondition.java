package com.rapleaf.jack.queries;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.ModelWithId;

public class JoinCondition implements IQueryCondition {
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

  @Override
  public String getSqlStatement() {
    String tableName = Utility.getTableName(model);
    return joinType.getSqlKeyword() + " " + tableName + " ON " + modelField1.getSqlKeyword() + " = " + modelField2.getSqlKeyword();
  }
}
