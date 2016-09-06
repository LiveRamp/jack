package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

import com.rapleaf.jack.exception.InvalidIndexHintException;

public final class IndexHint implements QueryCondition {

  public enum Type {
    USE, FORCE, IGNORE
  }

  public enum Scope {
    ALL("INDEX"),
    JOIN("INDEX FOR JOIN"),
    ORDER_BY("INDEX FOR ORDER BY"),
    GROUP_BY("INDEX FOR GROUP BY");

    private String sqlStatement;

    Scope(String sqlStatement) {
      this.sqlStatement = sqlStatement;
    }

    String getSqlStatement() {
      return sqlStatement;
    }
  }

  private Type type;
  private Scope scope;
  private List<String> indexNames;

  public IndexHint(Type type, Scope scope, Index... indices) {
    this.type = type;
    this.scope = scope;
    this.indexNames = Arrays.stream(indices).map(Index::getName).collect(Collectors.toList());
    Collections.sort(indexNames);
  }

  public Type getType() {
    return type;
  }

  public Scope getScope() {
    return scope;
  }

  static Set<IndexHint> validate(Set<IndexHint> indexHints) {
    boolean hasUse = false;
    boolean hasForce = false;
    for (IndexHint indexHint : indexHints) {
      hasUse = hasUse || indexHint.getType() == IndexHint.Type.USE;
      hasForce = hasForce || indexHint.getType() == IndexHint.Type.FORCE;
    }
    if (hasUse && hasForce) {
      throw new InvalidIndexHintException("USE INDEX and FORCE INDEX cannot be mixed for the same table");
    }
    return indexHints;
  }

  @Override
  public String getSqlStatement() {
    return type.name() + " " + scope.getSqlStatement() + " (" + Joiner.on(",").join(indexNames) + ")";
  }

  @Override
  public String toString() {
    return getSqlStatement();
  }

  @Override
  public int hashCode() {
    return getSqlStatement().hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || !(object instanceof IndexHint)) {
      return false;
    }
    return this.getSqlStatement().equals(((IndexHint)object).getSqlStatement());
  }

}
