package com.rapleaf.jack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ModelQuery {

  private List<QueryConstraint> constraints;
  private Set<Long> ids;

  public ModelQuery() {
    this.constraints = new ArrayList<QueryConstraint>();
    this.ids = new HashSet<Long>();
  }

  public String getSqlStatement() {

    return getIdSetSqlCondition() + " AND " + getConstraintListSqlCondition();
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

}
