package com.rapleaf.jack.queries;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.ModelWithId;

public class JoinConditionBuilder {
  private final GenericQueryBuilder queryBuilder;
  private final JoinType joinType;
  private final Class<? extends ModelWithId> model;
  private Optional<String> alias;

  JoinConditionBuilder(GenericQueryBuilder queryBuilder, JoinType joinType, Class<? extends ModelWithId> model) {
    this.queryBuilder = queryBuilder;
    this.joinType = joinType;
    this.model = model;
    this.alias = Optional.absent();
  }

  public JoinConditionBuilder as(String alias) {
    Preconditions.checkArgument(alias != null && !alias.isEmpty());
    this.alias = Optional.of(alias);
    return this;
  }

  public GenericQueryBuilder on(ModelField modelField1, ModelField modelField2) {
    queryBuilder.addJoinCondition(new JoinCondition(joinType, model, alias, modelField1, modelField2));
    return queryBuilder;
  }
}
