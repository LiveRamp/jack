package com.rapleaf.jack.generic_queries;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.ModelWithId;
import com.rapleaf.jack.queries.LimitCriterion;
import com.rapleaf.jack.queries.ModelQuery;
import com.rapleaf.jack.queries.OrderCriterion;
import com.rapleaf.jack.queries.WhereConstraint;

public class GenericQueryBuilder {

  private final GenericQuery genericQuery;
  private final BaseDatabaseConnection dbConnection;

  public GenericQueryBuilder(BaseDatabaseConnection dbConnection, GenericQuery genericQuery) {
    this.dbConnection = dbConnection;
    this.genericQuery = genericQuery;
  }

  public GenericQueryBuilder where(WhereCondition whereCondition) {
    genericQuery.addWhereCondition(whereCondition);
    return this;
  }

  public GenericQueryBuilder join(JoinCondition joinCondition) {
    genericQuery.addJoinCondition(joinCondition);
    return this;
  }

  public Set<Map<Class<? extends ModelWithId>, Map<Enum, Object>>> find() throws IOException {
    return null;
  }

  public List<Map<Class<? extends ModelWithId>, Map<Enum, Object>>> findWithOrder() throws IOException {
    return null;
  }
}
