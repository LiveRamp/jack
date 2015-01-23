package com.rapleaf.jack.generic_queries;

import com.rapleaf.jack.IModelField;
import com.rapleaf.jack.ModelWithId;

public class JoinCondition implements QueryCondition {
  private final Class<? extends ModelWithId> model;
  private final IModelField IModelField1;
  private final IModelField IModelField2;

  public JoinCondition(Class<? extends ModelWithId> model, IModelField IModelField1, IModelField IModelField2) {
    this.model = model;
    this.IModelField1 = IModelField1;
    this.IModelField2 = IModelField2;
  }

  public Class<? extends ModelWithId> getModel() {
    return model;
  }

  @Override
  public String getSqlStatement() {
    return "JOIN " + Utility.getTableName(model) + " ON " + IModelField1.getSqlKeyword() + " = " + IModelField2.getSqlKeyword();
  }
}
