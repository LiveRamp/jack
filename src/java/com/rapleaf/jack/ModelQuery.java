package com.rapleaf.jack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ModelQuery {

  private List<WhereConstraint> whereConstraints;
  private List<OrderCriterion> orderCriteria;
  private List<Enum> selectedFields;
  private LimitCriterion limitCriterion;
  private Set<Long> ids;

  public ModelQuery() {
    this.whereConstraints = new ArrayList<WhereConstraint>();
    this.orderCriteria = new ArrayList<OrderCriterion>();
    this.selectedFields = new ArrayList<Enum>();
    this.ids = new HashSet<Long>();
  }

  public List<WhereConstraint> getWhereConstraints() {
    return whereConstraints;
  }

  public List<OrderCriterion> getOrderCriteria() {
    return orderCriteria;
  }

  public List<Enum> getSelectedFields() {
    return selectedFields;
  }

  public Set<Long> getIdSet() {
    return ids;
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

  public String getWhereClause() {
    StringBuilder statementBuilder = new StringBuilder("WHERE (");
    statementBuilder.append(ids.isEmpty() ? "" : getIdSetSqlCondition());
    if (!ids.isEmpty() && !whereConstraints.isEmpty()) {
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

    if (selectedFields.isEmpty()) {
      sqlClause.append("*");
      return sqlClause.toString();
    }

    sqlClause.append("id, ");
    Iterator<Enum> it = selectedFields.iterator();
    while (it.hasNext()) {
      Enum field = it.next();
      sqlClause.append(field.name());
      if (it.hasNext()) {
        sqlClause.append(", ");
      }
    }
    return sqlClause.toString();
  }

  public void addSelectedFields(Enum... fields) {
    selectedFields.addAll(Arrays.asList(fields));
  }

  public void addSelectedField(Enum field) {
    selectedFields.add(field);
  }
}
