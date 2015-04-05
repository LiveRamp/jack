package com.rapleaf.jack.queries.where_operators;

import org.apache.commons.lang.StringUtils;

import com.rapleaf.jack.queries.Column;

public class Between<V> extends WhereOperator<V> {

  public Between(V value1, V value2) {
    super("BETWEEN ? AND ?", value1, value2);
    if (value1 instanceof Column) {
      this.sqlStatement = StringUtils.replaceOnce(this.sqlStatement, "?", ((Column)value1).getSqlKeyword());
    }
    if (value2 instanceof Column) {
      this.sqlStatement = StringUtils.replaceOnce(this.sqlStatement, " AND ?", " AND " + ((Column)value2).getSqlKeyword());
    }
  }
}
