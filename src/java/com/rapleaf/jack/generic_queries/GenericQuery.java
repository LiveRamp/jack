package com.rapleaf.jack.generic_queries;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.ModelWithId;

public class GenericQuery {

  private final BaseDatabaseConnection dbConnection;
  private final List<Class<? extends ModelWithId>> includedModels;
  private final Set<WhereCondition> whereConditions;
  private final List<JoinCondition> joinConditions;

  private GenericQuery(BaseDatabaseConnection dbConnection) {
    this.dbConnection = dbConnection;
    this.includedModels = Lists.newArrayList();
    this.whereConditions = Sets.newHashSet();
    this.joinConditions = Lists.newArrayList();
  }

  public static GenericQuery create(BaseDatabaseConnection dbConnection) {
    return new GenericQuery(dbConnection);
  }

  public GenericQueryBuilder from(Class<? extends ModelWithId> model) {
    includedModels.add(model);
    return new GenericQueryBuilder(dbConnection, this);
  }

  public void addWhereCondition(WhereCondition whereCondition) {
    whereConditions.add(whereCondition);
  }

  public void addJoinCondition(JoinCondition joinCondition) {
    includedModels.add(joinCondition.getModel());
    joinConditions.add(joinCondition);
  }
}
