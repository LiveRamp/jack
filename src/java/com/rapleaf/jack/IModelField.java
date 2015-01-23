package com.rapleaf.jack;

import com.rapleaf.jack.generic_queries.Utility;

public interface IModelField {

  public static String DEFAULT_ID_FIELD = "id";

  private final Class<? extends ModelWithId> model;
  private final Enum field;

  private IModelField(Class<? extends ModelWithId> model, Enum field) {
    this.model = model;
    this.field = field;
  }

  public static IModelField fieldKey(Class<? extends ModelWithId> model) {
    return field(model, null);
  }

  public static IModelField field(Class<? extends ModelWithId> model, Enum field) {
    return new IModelField(model, field);
  }

  public Class<? extends ModelWithId> getModel() {
    return model;
  }

  public Enum getField() {
    return field;
  }

  public String getSqlKeyword() {
    // TODO: get table name from model name
    return Utility.getTableName(model) + "." + (field != null ? field.toString() : DEFAULT_ID_FIELD);
  }
}
