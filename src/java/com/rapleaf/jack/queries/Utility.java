package com.rapleaf.jack.queries;

import com.rapleaf.jack.ModelWithId;

public class Utility {

  public static String getTableName(Class<? extends ModelWithId> model) {
    try {
      return (String)model.getDeclaredField("_TABLE_NAME").get(null);
    } catch (Exception e) {
      throw new RuntimeException("Static field _TABLE_NAME is missing for " + model.getSimpleName());
    }
  }
}
