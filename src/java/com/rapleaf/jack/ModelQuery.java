package com.rapleaf.jack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ModelQuery {

  private List<QueryConstraint> constraints;
  private ArrayList<QueryOrderConstraint> orderConstraints; 
  private Set<Long> ids;

  public ModelQuery() {
    this.constraints = new ArrayList<QueryConstraint>();
    orderConstraints = new ArrayList<QueryOrderConstraint>();
    this.ids = new HashSet<Long>();
  }

  public List<QueryConstraint> getConstraints() {
    return constraints;
  }

  public Set<Long> getIdSet() {
    return ids;
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

  public void addOrder(QueryOrderConstraint orderConstraint) {
    orderConstraints.add(orderConstraint);
  }

  public boolean isOrderedQuery() {
    return !orderConstraints.isEmpty();
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
  	if (!orderConstraints.isEmpty()) {
  	  sb.append("ORDER BY ");  	  
  	  Iterator<QueryOrderConstraint> it = orderConstraints.iterator();
  	  while (it.hasNext()) {
  	    QueryOrderConstraint orderConstraint = it.next();
  	    sb.append(orderConstraint.getSqlStatement());
  	    
  	    if (it.hasNext()) {
  	      sb.append(", ");
  	    }
  	  }
  	}  	
  	return sb.toString();
  }
}
