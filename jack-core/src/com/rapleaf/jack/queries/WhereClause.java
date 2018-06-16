package com.rapleaf.jack.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

public class WhereClause {
  List<WhereConstraint> whereConstraints;
  Optional<Set<Long>> selectedIds;

  public WhereClause() {
    this.whereConstraints = new ArrayList<>();
    this.selectedIds = Optional.absent();
  }

  public List<WhereConstraint> getWhereConstraints() {
    return whereConstraints;
  }

  public Optional<Set<Long>> getIdSet() {
    return selectedIds;
  }

  public void addConstraint(WhereConstraint constraint) {
    whereConstraints.add(constraint);
  }

  public void addIds(Collection<Long> ids) {
    if (!selectedIds.isPresent()) {
      selectedIds = Optional.<Set<Long>>of(Sets.<Long>newHashSet());
    }
    this.selectedIds.get().addAll(ids);
  }

  public void addId(Long id) {
    addIds(Collections.singleton(id));
  }

  public String getSqlString() {
    StringBuilder statementBuilder = new StringBuilder();
    if (selectedIds.isPresent() || !whereConstraints.isEmpty()) {
      statementBuilder.append("WHERE (");

      statementBuilder.append(getIdSetSqlCondition());
      if (selectedIds.isPresent() && !whereConstraints.isEmpty()) {
        statementBuilder.append(" AND ");
      }
      statementBuilder.append(getWhereSqlCriteria());
      statementBuilder.append(") ");
    }
    return statementBuilder.toString();
  }

  private String getIdSetSqlCondition() {
    if (!selectedIds.isPresent()) {
      return "";
    }

    Set<Long> ids = selectedIds.get();

    StringBuilder sb = new StringBuilder("id in (");
    if (ids.isEmpty()) {
      sb.append("null");
    } else {
      Iterator<Long> idIterator = selectedIds.get().iterator();
      while (idIterator.hasNext()) {
        Long id = idIterator.next();
        sb.append(id);
        if (idIterator.hasNext()) {
          sb.append(",");
        }
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
      sb.append("(").append(constraint.getSqlStatement()).append(")");

      if (it.hasNext()) {
        sb.append(" AND ");
      }
    }
    return sb.toString();
  }

  public boolean hasIdsOnly() {
    return this.whereConstraints.isEmpty();
  }

  @Override
  public String toString() {
    return this.getSqlString();
  }
}