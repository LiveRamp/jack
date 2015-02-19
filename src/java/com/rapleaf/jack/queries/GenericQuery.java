package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.tools.javac.util.Pair;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.ModelWithId;

public class GenericQuery {

  private final BaseDatabaseConnection dbConnection;
  private final List<Pair<Class<? extends ModelWithId>, String>> queryModels;
  private final List<JoinCondition> joinConditions;
  private final List<WhereConstraint> whereConstraints;
  private final List<OrderCriterion> orderCriteria;
  private final Set<ModelField> selectedModelFields;
  private final Set<ModelField> groupByModelFields;
  private Optional<LimitCriterion> limitCriteria;

  private GenericQuery(BaseDatabaseConnection dbConnection) {
    this.dbConnection = dbConnection;
    this.queryModels = Lists.newArrayList();
    this.joinConditions = Lists.newArrayList();
    this.whereConstraints = Lists.newArrayList();
    this.orderCriteria = Lists.newArrayList();
    this.selectedModelFields = Sets.newHashSet();
    this.groupByModelFields = Sets.newHashSet();
    this.limitCriteria = Optional.absent();
  }

  public static GenericQuery create(BaseDatabaseConnection dbConnection) {
    return new GenericQuery(dbConnection);
  }

  public GenericQueryBuilder from(Class<? extends ModelWithId> model) {
    this.queryModels.add(Pair.<Class<? extends ModelWithId>, String>of(model, null));
    return new GenericQueryBuilder(dbConnection, this);
  }

  void addSelectedModelField(ModelField modelField, ModelField... modelFields) {
    this.selectedModelFields.add(modelField);
    this.selectedModelFields.addAll(Arrays.asList(modelFields));
  }

  Set<ModelField> getSelectedModelFields() {
    return selectedModelFields;
  }

  void addJoinCondition(JoinCondition joinCondition) {
    this.queryModels.add(Pair.<Class<? extends ModelWithId>, String>of(joinCondition.getModel(), joinCondition.getModelAlias()));
    this.joinConditions.add(joinCondition);
  }

  void addWhereCondition(WhereConstraint whereConstraint) {
    this.whereConstraints.add(whereConstraint);
  }

  List<WhereConstraint> getWhereConstraints() {
    return whereConstraints;
  }

  void addOrderCondition(OrderCriterion orderCriterion) {
    this.orderCriteria.add(orderCriterion);
  }

  void addLimitCondition(LimitCriterion limitCriterion) {
    this.limitCriteria = Optional.of(limitCriterion);
  }

  void addGroupByModelFields(ModelField modelField, ModelField... modelFields) {
    this.groupByModelFields.add(modelField);
    this.groupByModelFields.addAll(Arrays.asList(modelFields));
  }

  String getSqlStatement() {
    return getSelectClause()
        + getJoinClause()
        + getWhereClause()
        + getGroupByClause()
        + getOrderClause()
        + getLimitClause();
  }

  private String getSelectClause() {
    if (selectedModelFields.isEmpty()) {
      selectAllModelFields();
    }

    StringBuilder clause = new StringBuilder("SELECT ");
    Iterator<ModelField> it = selectedModelFields.iterator();
    while (it.hasNext()) {
      clause.append(it.next().getSqlKeyword());
      if (it.hasNext()) {
        clause.append(", ");
      }
    }

    String tableName = Utility.getTableName(queryModels.get(0).fst);
    return clause.append(" FROM ").append(tableName).append(" ").toString();
  }

  private String getJoinClause() {
    return getClause(joinConditions, "", " ");
  }

  private String getWhereClause() {
    return getClause(whereConstraints, "WHERE ", " ");
  }

  private String getGroupByClause() {
    if (groupByModelFields.isEmpty()) {
      return "";
    }

    StringBuilder groupByClause = new StringBuilder("GROUP BY ");
    Iterator<ModelField> it = groupByModelFields.iterator();
    while (it.hasNext()) {
      groupByClause.append(it.next().getSqlKeyword());
      if (it.hasNext()) {
        groupByClause.append(", ");
      }
    }

    return groupByClause.append(" ").toString();
  }

  private String getOrderClause() {
    if (orderCriteria.isEmpty()) {
      return "";
    } else {
      return getClause(orderCriteria, "ORDER BY ", ", ");
    }
  }

  private String getLimitClause() {
    if (limitCriteria.isPresent()) {
      return limitCriteria.get().getSqlStatement() + " ";
    } else {
      return "";
    }
  }

  private void selectAllModelFields() {
    for (Pair<Class<? extends ModelWithId>, String> modelAndAlias : queryModels) {
      Class<? extends ModelWithId> model = modelAndAlias.fst;
      String alias = modelAndAlias.snd;

      Set<ModelField> modelFields;
      try {
        modelFields = (Set<ModelField>)model.getDeclaredField("_ALL_MODEL_FIELDS").get(null);
      } catch (Exception e) {
        throw new RuntimeException("Cannot get the static field _ALL_MODEL_FIELDS for " + model.getSimpleName());
      }

      for (ModelField modelField : modelFields) {
        selectedModelFields.add(alias == null ? modelField : modelField.of(alias));
      }
    }
  }

  private <T extends IQueryCondition> String getClause(Collection<T> conditions, String initialKeyword, String separator) {
    if (conditions.isEmpty()) {
      return "";
    }

    StringBuilder clause = new StringBuilder(initialKeyword);
    Iterator<T> it = conditions.iterator();
    while (it.hasNext()) {
      clause.append(it.next().getSqlStatement());
      if (it.hasNext()) {
        clause.append(separator);
      }
    }

    return clause.append(" ").toString();
  }
}
