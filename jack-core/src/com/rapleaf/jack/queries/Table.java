package com.rapleaf.jack.queries;

import java.util.Set;

import com.rapleaf.jack.AttributesWithId;
import com.rapleaf.jack.ModelWithId;

public interface Table<A extends AttributesWithId, M extends ModelWithId> {

  public String getName();

  public String getAlias();

  public Set<Column> getAllColumns();

  public String getSqlKeyword();

  public Class<A> getAttributesType();

  public Class<M> getModelType();

}
