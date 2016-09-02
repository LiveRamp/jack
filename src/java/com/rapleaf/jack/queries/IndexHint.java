package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import com.rapleaf.jack.util.JackUtility;

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

  public IndexHint(Type type, Scope scope, Index index, Index... indices) {
    this.type = type;
    this.scope = scope;
    this.indexNames = Lists.newArrayList(index.getName());
    this.indexNames.addAll(Collections2.transform(Arrays.asList(indices), JackUtility.INDEX_NAME_EXTRACTOR));
  }

  @Override
  public String getSqlStatement() {
    return type.name() + " " + scope.getSqlStatement() + " (" + Joiner.on(",").join(indexNames) + ")";
  }

}
