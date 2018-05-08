package com.rapleaf.jack.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;

public class ModelQuery {

  private WhereClause whereClause;
  private List<OrderCriterion> orderCriteria;
  private List<FieldSelector> selectedFields;
  private List<Enum> groupByFields;
  private Optional<LimitCriterion> limitCriterion;

  public ModelQuery() {
    this.whereClause = new WhereClause();
    this.orderCriteria = new ArrayList<>();
    this.selectedFields = new ArrayList<>();
    this.groupByFields = new ArrayList<>();
    //By default, no limit criteria
    this.limitCriterion = Optional.absent();
  }

  public List<FieldSelector> getSelectedFields() {
    return selectedFields;
  }

  public List<WhereConstraint> getWhereConstraints() {
    return whereClause.getWhereConstraints();
  }

  public Optional<Set<Long>> getIdSet() {
    return whereClause.getIdSet();
  }

  public List<OrderCriterion> getOrderCriteria() {
    return orderCriteria;
  }

  public Optional<LimitCriterion> getLimitCriterion() {
    return limitCriterion;
  }

  public void setLimitCriterion(LimitCriterion limitCriterion) {
    this.limitCriterion = Optional.of(limitCriterion);
  }

  public void addConstraint(WhereConstraint constraint) {
    whereClause.addConstraint(constraint);
  }

  public void addIds(Collection<Long> ids) {
    whereClause.addIds(ids);
  }

  public void addId(Long id) {
    whereClause.addId(id);
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

  public WhereClause getWhereClause() {
    return whereClause;
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
    if (limitCriterion.isPresent()) {
      return limitCriterion.get().getSqlStatement();
    }
    return "";
  }

  public boolean isOnlyIdQuery() {
    return whereClause.hasIdsOnly()
        && selectedFields.isEmpty()
        && orderCriteria.isEmpty()
        && !limitCriterion.isPresent();
  }
}
