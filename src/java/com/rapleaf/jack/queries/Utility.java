package com.rapleaf.jack.queries;

import java.util.Map;

import com.google.common.collect.Maps;

import com.rapleaf.jack.ModelWithId;

public class Utility {
  private static Map<Class<? extends ModelWithId>, String> cachedTableNames = Maps.newHashMap();

  public static String getTableName(Class<? extends ModelWithId> model) {
    try {
      model.getSimpleName();
      String tableName = cachedTableNames.get(model);
      if (tableName == null) {
        tableName = (String)model.getDeclaredField("_TABLE_NAME").get(null);
        cachedTableNames.put(model, tableName);
      }
      return tableName;
    } catch (Exception e) {
      throw new RuntimeException("Static field _TABLE_NAME is missing for " + model.getSimpleName());
    }
  }
}
