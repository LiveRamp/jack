package com.rapleaf.jack.test_project.database_1;

import com.rapleaf.jack.DatabaseConnection;
import com.rapleaf.jack.queries.AbstractGenericQuery;
import com.rapleaf.jack.queries.Table;

public class Database1Query extends AbstractGenericQuery {
  private Database1Query(Table table) {
    super(new DatabaseConnection("database1"), table);
  }

  public static AbstractGenericQuery from(Table table) {
    return new Database1Query(table);
  }
}
