package com.rapleaf.jack.generic_queries;

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
  private List<JoinCondition> joinConditions;
  private List<WhereCondition> whereConditions;
  private Set<OrderCondition> orderConditions;
  private Set<ModelField> selectedIModelFields;
  private Optional<LimitCondition> limitCondition;

  private GenericQuery(BaseDatabaseConnection dbConnection) {
    this.dbConnection = dbConnection;
    this.mainModel = null;
    this.joinConditions = Lists.newArrayList();
    this.whereConditions = Lists.newArrayList();
    this.orderConditions = Sets.newHashSet();
    this.selectedIModelFields = Sets.newHashSet();
    this.limitCondition = Optional.absent();
  }

  public static GenericQuery create(BaseDatabaseConnection dbConnection) {
    return new GenericQuery(dbConnection);
  }

  public GenericQueryBuilder from(Class<? extends ModelWithId> model) {
    mainModel = model;
    return new GenericQueryBuilder(dbConnection, this);
  }

  List<WhereCondition> getWhereConditions() {
    return whereConditions;
  }

  Set<ModelField> getSelectedIModelFields() {
    return selectedIModelFields;
  }

  void addJoinCondition(JoinCondition joinCondition) {
    joinConditions.add(joinCondition);
  }

  void addWhereCondition(WhereCondition whereCondition) {
    whereConditions.add(whereCondition);
  }

  void addOrderCondition(OrderCondition orderCondition) {
    orderConditions.add(orderCondition);
  }

  void addLimitCondition(LimitCondition lmtCondition) {
    this.limitCondition = Optional.of(lmtCondition);
  }

  void addSelectedModelField(ModelField modelField) {
    selectedIModelFields.add(modelField);
  }

  boolean isOrderedQuery() {
    return !orderConditions.isEmpty();
  }

  String getSqlStatement() {
    StringBuilder statement = new StringBuilder();

    statement.append(getSelectClause())
        .append(getJoinClause())
        .append(getWhereClause());

    if (isOrderedQuery()) {
      statement.append(getOrderClause());
      if (!orderConditions.isEmpty()) {
        statement.append(getLimitClause());
      }
    }

    return statement.toString();
  }

  private String getSelectClause() {
    StringBuilder clause = new StringBuilder("SELECT ");

    if (selectedIModelFields.isEmpty()) {
      clause.append("*");
    } else {
      Iterator<ModelField> it = selectedIModelFields.iterator();
      while (it.hasNext()) {
        clause.append(it.next().getFullSqlKeyword());
        if (it.hasNext()) {
          clause.append(", ");
        }
      }
    }

    String tableName = Utility.getTableNameFromModel(mainModel);
    return clause.append(" FROM ").append(tableName).append(" ").toString();
  }

  private String getJoinClause() {
    return getClause(joinConditions, "", " ");
  }

  private String getWhereClause() {
    return getClause(whereConditions, "WHERE ", " AND ");
  }

  private String getOrderClause() {
    return getClause(orderConditions, "ORDER BY ", ", ");
  }

  private String getLimitClause() {
    if (limitCondition.isPresent()) {
      return limitCondition.get().getSqlStatement() + " ";
    } else {
      return "";
    }
  }

  private <T extends QueryCondition> String getClause(Collection<T> conditions, String initialKeyWord, String separator) {
    if (conditions.isEmpty()) {
      return "";
    }

    StringBuilder clause = new StringBuilder(initialKeyWord);
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
