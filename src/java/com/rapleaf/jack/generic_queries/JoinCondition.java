package com.rapleaf.jack.generic_queries;

import com.rapleaf.jack.ModelWithId;

public class JoinCondition {
  private final Class<? extends ModelWithId> model;
  private final ModelField field1;
  private final ModelField field2;

  public JoinCondition(Class<? extends ModelWithId> model, ModelField field1, ModelField field2) {
    this.model = model;
    this.field1 = field1;
    this.field2 = field2;
  }

  public Class<? extends ModelWithId> getModel() {
    return model;
  }

  public String getSqlStatement() {
    return "join " + model.getSimpleName() + field1.getSqlKeyword() + " = " + field2.getSqlKeyword();
  }
}
