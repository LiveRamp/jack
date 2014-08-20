package com.rapleaf.jack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ModelQuery {

  private List<WhereConstraint> constraints;
  private List<OrderCriterion> orderCriteria;
  private List<SelectCriterion> selectCriteria;
  private LimitCriterion limitCriterion;
  private Set<Long> ids;

  public ModelQuery() {
    this.constraints = new ArrayList<WhereConstraint>();
    this.orderCriteria = new ArrayList<OrderCriterion>();
    this.selectCriteria = new ArrayList<SelectCriterion>();
    this.ids = new HashSet<Long>();
  }

  public List<WhereConstraint> getConstraints() {
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

  public void addConstraint(WhereConstraint constraint) {
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
    StringBuilder statementBuilder = new StringBuilder("WHERE (");
    statementBuilder.append(ids.isEmpty() ? "" : getIdSetSqlCondition());
    if (!ids.isEmpty() && !constraints.isEmpty()) {
      statementBuilder.append(" AND ");
    }
    statementBuilder.append(getWhereSqlCriteria());
    statementBuilder.append(")");
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
    Iterator<WhereConstraint> it = constraints.iterator();
    while (it.hasNext()) {
      WhereConstraint constraint = it.next();
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
    return limitCriterion.getSqlClause();
  }

  public LimitCriterion getLimitCriterion() {
    return limitCriterion;
  }

  public String getSelectClause() {
    StringBuilder sqlClause = new StringBuilder("SELECT ");

    if (selectCriteria.isEmpty()) {
      sqlClause.append("*");
      return sqlClause.toString();
    }

    Iterator<SelectCriterion> it = selectCriteria.iterator();
    while (it.hasNext()) {
      SelectCriterion selectCriterion = it.next();
      sqlClause.append(selectCriterion.getSqlClause());
      if (it.hasNext()) {
        sqlClause.append(", ");
      }
    }
    return sqlClause.toString();
  }

  public void addSelectCriteria(SelectCriterion... criteria) {
    selectCriteria.addAll(Arrays.asList(criteria));
  }

  public void addSelectCriterion(SelectCriterion criterion) {
    selectCriteria.add(criterion);
  }
}
