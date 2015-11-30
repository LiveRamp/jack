package com.rapleaf.jack.queries;

import java.util.Set;

import com.rapleaf.jack.AttributesWithId;
import com.rapleaf.jack.ModelWithId;

public interface Table {

  public String getName();

  public String getAlias();

  public Set<Column> getAllColumns();

  public String getSqlKeyword();

  public <A extends AttributesWithId> Class<A> getAttributeType();

  public <M extends ModelWithId> Class<M> getModelType();

}
