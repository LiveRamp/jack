package com.rapleaf.jack.queries;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.ModelWithId;

public class DbQuery {

  private final BaseDatabaseConnection dbConnection;
  private final ModelQuery query;

  public DbQuery(BaseDatabaseConnection dbConnection) {
    this.dbConnection = dbConnection;
    this.query = new ModelQuery(null);
  }

  public DbQueryBuilder from(Class<? extends ModelWithId> model) {
    return new DbQueryBuilder(dbConnection, model);
  }
}
