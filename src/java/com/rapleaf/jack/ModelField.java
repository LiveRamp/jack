package com.rapleaf.jack;

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

  public static ModelField field(Class<? extends ModelWithId> table, Enum field, Class fieldType) {
    return new ModelField(table, field, fieldType);
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

  public String getFullSqlKeyword() {
    return model + "." + getSimpleSqlKeyword();
  }

  public String getSimpleSqlKeyword() {
    return field != null ? field.toString() : DEFAULT_ID_FIELD;
  }

  @Override
  public String toString() {
    return getFullSqlKeyword();
  }

  @Override
  public int hashCode() {
    return getFullSqlKeyword().hashCode();
  }

  @Override
  public boolean equals(Object that) {
    return that instanceof ModelField && this.toString().equals(((ModelField)that).toString());
  }
}
