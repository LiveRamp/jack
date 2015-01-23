package com.rapleaf.jack;

import com.rapleaf.jack.generic_queries.Utility;

public class ModelField {

  public static String DEFAULT_ID_FIELD = "id";

  private final Class<? extends ModelWithId> model;
  private final Enum field;
  private final Class fieldType;

  private ModelField(Class<? extends ModelWithId> model, Enum field, Class fieldType) {
    this.model = model;
    this.field = field;
    this.fieldType = fieldType;
  }

  public static ModelField fieldKey(Class<? extends ModelWithId> model) {
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

  public Class getFieldType() {
    return fieldType;
  }

  public String getSqlKeyword() {
    return Utility.getTableName(model) + "." + (field != null ? field.toString() : DEFAULT_ID_FIELD);
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
