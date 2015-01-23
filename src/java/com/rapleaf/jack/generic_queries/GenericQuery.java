package com.rapleaf.jack.generic_queries;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.IModelField;
import com.rapleaf.jack.ModelWithId;

public class GenericQuery {

  private final BaseDatabaseConnection dbConnection;
  private final List<Class<? extends ModelWithId>> includedModels;
  private final List<JoinCondition> joinConditions;
  private final Set<WhereCondition> whereConditions;
  private final Set<OrderCondition> orderConditions;
  private final Set<IModelField> selectedIModelFields;
  private Optional<LimitCondition> limitCondition;

  private GenericQuery(BaseDatabaseConnection dbConnection) {
    this.dbConnection = dbConnection;
    this.includedModels = Lists.newArrayList();
    this.joinConditions = Lists.newArrayList();
    this.whereConditions = Sets.newHashSet();
    this.orderConditions = Sets.newHashSet();
    this.selectedIModelFields = Sets.newHashSet();
    this.limitCondition = Optional.absent();
  }

  public static GenericQuery create(BaseDatabaseConnection dbConnection) {
    return new GenericQuery(dbConnection);
  }

  public GenericQueryBuilder from(Class<? extends ModelWithId> model) {
    includedModels.add(model);
    return new GenericQueryBuilder(dbConnection, this);
  }

  void addJoinCondition(JoinCondition joinCondition) {
    includedModels.add(joinCondition.getModel());
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

  void addSelectedModelField(IModelField IModelField) {
    selectedIModelFields.add(IModelField);
  }

  String getSqlStatement(boolean isOrderedQuery) {
    StringBuilder statement = new StringBuilder();

    statement.append(getSelectClause())
        .append(getJoinClause())
        .append(getWhereClause());

    if (isOrderedQuery) {
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
      Iterator<IModelField> it = selectedIModelFields.iterator();
      while (it.hasNext()) {
        clause.append(it.next().getSqlKeyword());
        if (it.hasNext()) {
          clause.append(", ");
        }
      }
    }

    return clause.append(" FROM ").append(Utility.getTableName(includedModels.get(0))).append(" ").toString();
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
