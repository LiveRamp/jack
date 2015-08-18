package com.rapleaf.jack.queries;

import java.util.Set;

import com.rapleaf.jack.AttributesWithId;
import com.rapleaf.jack.ModelWithId;

public interface Table {

  public Set<Column> getAllColumns();

  public String getSqlKeyword();

  public Class<? extends AttributesWithId> getAttributeType();

  public Class<? extends ModelWithId> getModelType();

}
