package com.rapleaf.jack.queries;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;

import com.rapleaf.jack.util.JackUtility;

public final class IndexHint {

  public enum Type {
    USE, FORCE, IGNORE
  }

  public enum Scope {
    ALL(""),
    JOIN("FOR JOIN"),
    ORDER_BY("FOR ORDER BY"),
    GROUP_BY("FOR GROUP BY");

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

  private IndexHint(Type type, Scope scope, List<Index> indices) {
    this.type = type;
    this.scope = scope;
    this.indexNames = FluentIterable.from(indices).transform(JackUtility.INDEX_NAME_EXTRACTOR).toImmutableList();
  }

  public String getSqlStatement() {
    return type.name() + " INDEX " + scope.getSqlStatement() + " (" + Joiner.on(",").join(indexNames) + ")";
  }

}
