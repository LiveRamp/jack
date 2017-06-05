package com.rapleaf.jack;

public interface IAssociationMetadata<T extends ModelWithId, F extends ModelWithId> {

  AssociationType getType();

  Class<T> getModelClass();

  Class<F> getAssociatedModelClass();

  String getForeignKeyFieldName();
}
