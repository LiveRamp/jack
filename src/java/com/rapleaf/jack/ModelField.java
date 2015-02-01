package com.rapleaf.jack;

import com.rapleaf.jack.queries.Utility;

public class ModelField {
  private static String DEFAULT_ID_FIELD = "id";

  private final Class<? extends ModelWithId> model;
  private final Enum field;
  private final Class type;

  private ModelField(Class<? extends ModelWithId> model, Enum field, Class type) {
    this.model = model;
    this.field = field;
    this.type = type;
  }

  public static ModelField key(Class<? extends ModelWithId> model) {
    return field(model, null, Long.class);
  }

  public static ModelField field(Class<? extends ModelWithId> model, Enum field, Class fieldType) {
    return new ModelField(model, field, fieldType);
  }

  public Class<? extends ModelWithId> getModel() {
    return model;
  }

  public Enum getField() {
    return field;
  }

  public Class getType() {
    return type;
  }

  public String getSqlKeyword() {
    String fieldKeyword = field != null ? field.toString() : DEFAULT_ID_FIELD;
    if (model != null) {
      return Utility.getTableName(model) + "." + fieldKeyword;
    } else {
      return fieldKeyword;
    }
  }

  @Override
  public String toString() {
    return getSqlKeyword();
  }

  @Override
  public int hashCode() {
    return getSqlKeyword().hashCode();
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof ModelField && this.toString().equals(((ModelField)that).toString());
  }
}