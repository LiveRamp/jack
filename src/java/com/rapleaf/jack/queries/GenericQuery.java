package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.ModelField;
import com.rapleaf.jack.ModelWithId;

public class GenericQuery {

  private final BaseDatabaseConnection dbConnection;
  private Class<? extends ModelWithId> mainModel;
  private final List<JoinCondition> joinConditions;
  private final List<WhereConstraint> whereConstraints;
  private final List<OrderCriterion> orderCriteria;
  private final Set<ModelField> selectedModelFields;
  private final Set<ModelField> groupByModelFields;
  private Optional<LimitCriterion> limitCriteria;

  private GenericQuery(BaseDatabaseConnection dbConnection) {
    this.dbConnection = dbConnection;
    this.mainModel = null;
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
    this.mainModel = model;
    return new GenericQueryBuilder(dbConnection, this);
  }

  List<WhereConstraint> getWhereConstraints() {
    return whereConstraints;
  }

  Set<ModelField> getSelectedModelFields() {
    return selectedModelFields;
  }

  void addJoinCondition(JoinCondition joinCondition) {
    this.joinConditions.add(joinCondition);
  }

  void addWhereCondition(WhereConstraint whereConstraint) {
    this.whereConstraints.add(whereConstraint);
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

  void addSelectedModelField(ModelField modelField) {
    this.selectedModelFields.add(modelField);
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
    StringBuilder clause = new StringBuilder("SELECT ");

    if (selectedModelFields.isEmpty()) {
      clause.append("*");
    } else {
      Iterator<ModelField> it = selectedModelFields.iterator();
      while (it.hasNext()) {
        clause.append(it.next().getSqlKeyword());
        if (it.hasNext()) {
          clause.append(", ");
        }
      }
    }

    String tableName = Utility.getTableName(mainModel);
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
