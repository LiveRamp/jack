package com.rapleaf.jack.generic_queries;

import com.google.common.base.CaseFormat;

import com.rapleaf.jack.ModelWithId;

public class Utility {

  public static String getTableName(Class<? extends ModelWithId> model) {
    // TODO: use generic pluralization
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, model.getSimpleName()) + "s";
  }

}
