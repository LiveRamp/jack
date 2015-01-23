package com.rapleaf.jack.generic_queries;

import com.rapleaf.jack.ModelWithId;

public class Utility {

  public static String getTableName(Class<? extends ModelWithId> model) {
    return model.getSimpleName().toLowerCase() + "s";
  }

}
