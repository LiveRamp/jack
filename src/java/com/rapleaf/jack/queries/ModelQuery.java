package com.rapleaf.jack.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ModelQuery {

  private List<WhereConstraint> whereConstraints;
  private List<OrderCriterion> orderCriteria;
  private List<FieldSelector> selectedFields;
  private List<Enum> groupByFields;
  private LimitCriterion limitCriterion;
  private Set<Long> ids;

  public ModelQuery() {
    this.whereConstraints = new ArrayList<WhereConstraint>();
    this.orderCriteria = new ArrayList<OrderCriterion>();
    this.selectedFields = new ArrayList<FieldSelector>();
    this.groupByFields = new ArrayList<Enum>();
    this.ids = new HashSet<Long>();
  }

  public List<FieldSelector> getSelectedFields() {
    return selectedFields;
  }

  public List<WhereConstraint> getWhereConstraints() {
    return whereConstraints;
  }

  public Set<Long> getIdSet() {
    return ids;
  }

  public List<OrderCriterion> getOrderCriteria() {
    return orderCriteria;
  }

  public LimitCriterion getLimitCriterion() {
    return limitCriterion;
  }

  public void setLimitCriterion(LimitCriterion limitCriterion) {
    this.limitCriterion = limitCriterion;
  }

  public void addConstraint(WhereConstraint constraint) {
    whereConstraints.add(constraint);
  }

  public void addIds(Set<Long> ids) {
    this.ids.addAll(ids);
  }

  public void addId(Long id) {
    ids.add(id);
  }

  public void addOrder(OrderCriterion orderCriterion) {
    orderCriteria.add(orderCriterion);
  }

  public void addSelectedField(FieldSelector fields) {
    selectedFields.add(fields);
  }

  public void addSelectedFields(FieldSelector... fields) {
    selectedFields.addAll(Arrays.asList(fields));
  }

  public void addGroupByFields(Enum... fields) {
    groupByFields.addAll(Arrays.asList(fields));
  }

  public String getSelectClause() {
    StringBuilder sqlClause = new StringBuilder("SELECT ");

    if (selectedFields.isEmpty()) {
      sqlClause.append("*");
      return sqlClause.toString();
    }

    sqlClause.append("id, ");
    Iterator<FieldSelector> iterator = selectedFields.iterator();
    while (iterator.hasNext()) {
      FieldSelector selector = iterator.next();
      sqlClause.append(selector.getSqlClause());
      if (iterator.hasNext()) {
        sqlClause.append(", ");
      }
    }
    return sqlClause.toString();
  }

  public String getWhereClause() {
    StringBuilder statementBuilder = new StringBuilder();
    if (!ids.isEmpty() || !whereConstraints.isEmpty()) {
      statementBuilder.append("WHERE (");

      statementBuilder.append(ids.isEmpty() ? "" : getIdSetSqlCondition());
      if (!ids.isEmpty() && !whereConstraints.isEmpty()) {
        statementBuilder.append(" AND ");
      }
      statementBuilder.append(getWhereSqlCriteria());
      statementBuilder.append(") ");
    }
    return statementBuilder.toString();
  }

  private String getIdSetSqlCondition() {
    StringBuilder sb = new StringBuilder("id in (");
    Iterator<Long> iter = ids.iterator();
    while (iter.hasNext()) {
      Long obj = iter.next();
      sb.append(obj.toString());
      if (iter.hasNext()) {
        sb.append(",");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  private String getWhereSqlCriteria() {
    StringBuilder sb = new StringBuilder();
    Iterator<WhereConstraint> it = whereConstraints.iterator();
    while (it.hasNext()) {
      WhereConstraint constraint = it.next();
      sb.append(constraint.getSqlStatement());

      if (it.hasNext()) {
        sb.append(" AND ");
      }
    }
    return sb.toString();
  }

  public String getGroupByClause() {
    StringBuilder sb = new StringBuilder();
    if (!groupByFields.isEmpty()) {
      sb.append("GROUP BY ");
      Iterator<Enum> it = groupByFields.iterator();
      while (it.hasNext()) {
        sb.append(it.next());
        if (it.hasNext()) {
          sb.append(", ");
        }
      }
      sb.append(" ");
    }
    return sb.toString();
  }

  public String getOrderByClause() {
    StringBuilder sb = new StringBuilder();
    if (!orderCriteria.isEmpty()) {
      sb.append("ORDER BY ");
      Iterator<OrderCriterion> it = orderCriteria.iterator();
      while (it.hasNext()) {
        OrderCriterion orderCriterion = it.next();
        sb.append(orderCriterion.getSqlStatement());
        if (it.hasNext()) {
          sb.append(", ");
        }
      }
      sb.append(" ");
    }
    return sb.toString();
  }

  public String getLimitClause() {
    if (limitCriterion == null) {
      return "";
    }
    return limitCriterion.getSqlClause();
  }

  public boolean isEmptyQuery() {
    return whereConstraints.isEmpty()
        && selectedFields.isEmpty();
  }
}
