package com.rapleaf.jack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ModelQuery {

  private List<QueryConstraint> constraints;
  private List<OrderCriterion> orderCriteria;
  private Set<Long> ids;
  private LimitCriterion limitCriterion;

  public ModelQuery() {
    this.constraints = new ArrayList<QueryConstraint>();
    this.orderCriteria = new ArrayList<OrderCriterion>();
    this.ids = new HashSet<Long>();
  }

  public List<QueryConstraint> getConstraints() {
    return constraints;
  }

  public List<OrderCriterion> getOrderCriteria() {
    return orderCriteria;
  }

  public Set<Long> getIdSet() {
    return ids;
  }

  public void setLimitCriterion(LimitCriterion limitCriterion) {
    this.limitCriterion = limitCriterion;
  }

  public void addConstraint(QueryConstraint constraint) {
    constraints.add(constraint);
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

  public String getWhereClause() {
    StringBuilder statementBuilder = new StringBuilder();
    statementBuilder.append(ids.isEmpty() ? "" : getIdSetSqlCondition());
    if (!ids.isEmpty() && !constraints.isEmpty()) {
      statementBuilder.append(" AND ");
    }
    statementBuilder.append(getConstraintListSqlCondition());

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

  private String getConstraintListSqlCondition() {
    StringBuilder sb = new StringBuilder();
    Iterator<QueryConstraint> it = constraints.iterator();
    while (it.hasNext()) {
      QueryConstraint constraint = it.next();
      sb.append(constraint.getSqlStatement());

      if (it.hasNext()) {
        sb.append(" AND ");
      }
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
    }
    return sb.toString();
  }

  public String getLimitClause() {
    if (limitCriterion == null) {
      return "";
    }
    return limitCriterion.getSqlKeyword();
  }

  public LimitCriterion getLimitCriterion() {
    return limitCriterion;
  }
}
