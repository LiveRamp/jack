package com.rapleaf.jack;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Utility;

public class ModelField {
  private static String DEFAULT_ID_FIELD = "id";

  protected final Class<? extends ModelWithId> model;
  protected String modelAlias;
  protected final Enum field;
  protected final Class type;

  protected ModelField(Class<? extends ModelWithId> model, String modelAlias, Enum field, Class type) {
    this.model = model;
    this.modelAlias = modelAlias;
    this.field = field;
    this.type = type;
  }

  protected ModelField(ModelField that) {
    this.model = that.model;
    this.modelAlias = that.modelAlias;
    this.field = that.field;
    this.type = that.type;
  }

  public static ModelField key(Class<? extends ModelWithId> model) {
    return new ModelField(model, null, null, Long.class);
  }

  public static ModelField field(Class<? extends ModelWithId> model, Enum field, Class fieldType) {
    return new ModelField(model, null, field, fieldType);
  }

  public ModelField of(String modelAlias) {
    Preconditions.checkArgument(modelAlias != null && !modelAlias.isEmpty());
    return new ModelField(model, modelAlias, field, type);
  }

  public Enum getField() {
    return field;
  }

  public Class getType() {
    return type;
  }

  public String getSqlKeyword() {
    StringBuilder sqlKeyword = new StringBuilder();

    if (modelAlias != null) {
      sqlKeyword.append(modelAlias).append(".");
    } else if (model != null) {
      sqlKeyword.append(Utility.getTableName(model)).append(".");
    }

    if (field != null) {
      sqlKeyword.append(field.toString());
    } else {
      sqlKeyword.append(DEFAULT_ID_FIELD);
    }

    return sqlKeyword.toString();
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
