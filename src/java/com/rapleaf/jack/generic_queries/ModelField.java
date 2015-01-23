package com.rapleaf.jack.generic_queries;

import com.rapleaf.jack.ModelWithId;

public class ModelField {

  public static String DEFAULT_ID_FIELD = "id";

  private final Class<? extends ModelWithId> model;
  private final Enum field;

  private ModelField(Class<? extends ModelWithId> model, Enum field) {
    this.model = model;
    this.field = field;
  }

  public static ModelField keyOf(Class<? extends ModelWithId> model) {
    return of(model, null);
  }

  public static ModelField of(Class<? extends ModelWithId> model, Enum field) {
    return new ModelField(model, field);
  }

  public Class<? extends ModelWithId> getModel() {
    return model;
  }

  public Enum getField() {
    return field;
  }

  public String getSqlKeyword() {
    // TODO: get table name from model name
    return model.getSimpleName().toLowerCase() + "s." + (field != null ? field.toString() : DEFAULT_ID_FIELD);
  }
}
