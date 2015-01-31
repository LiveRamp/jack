package com.rapleaf.jack.generic_queries;

import com.rapleaf.jack.ModelWithId;

public class Utility {

  public static String getTableNameFromModel(Class<? extends ModelWithId> model) {
    try {
      return  (String)model.getClass().getDeclaredField("_TABLE_NAME").get(null);
    } catch (Exception e) {
      throw new RuntimeException("Static field _TABLE_NAME is missing for " + model.getSimpleName());
    }
  }
}
