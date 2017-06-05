package com.rapleaf.jack;

public class DefaultAssociationMetadata<T extends ModelWithId,F extends ModelWithId> implements IAssociationMetadata<T,F> {


  private final AssociationType type;
  private final Class<T> model;
  private final Class<F> associatedModel;
  private final String keyFieldName;

  public DefaultAssociationMetadata(AssociationType type, Class<T> model, Class<F> associatedModel, String keyFieldName) {
    this.type = type;
    this.model = model;
    this.associatedModel = associatedModel;
    this.keyFieldName = keyFieldName;
  }

  @Override
  public AssociationType getType() {
    return this.type;
  }

  @Override
  public Class<T> getModelClass() {
    return this.model;
  }

  @Override
  public Class<F> getAssociatedModelClass() {
    return this.associatedModel;
  }

  @Override
  public String getForeignKeyFieldName() {
    return this.keyFieldName;
  }
}
