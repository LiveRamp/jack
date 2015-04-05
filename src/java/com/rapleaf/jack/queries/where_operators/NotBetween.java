package com.rapleaf.jack.queries.where_operators;

import org.apache.commons.lang.StringUtils;

import com.rapleaf.jack.queries.Column;

public class NotBetween<V> extends WhereOperator<V> {

  public NotBetween(V value1, V value2) {
    super("NOT BETWEEN ? AND ?");

    // this operator takes care of its own construction
    if (!(value1 instanceof Column)) {
      this.parameters.add(value1);
    } else {
      this.sqlStatement = StringUtils.replaceOnce(this.sqlStatement, "?", ((Column)value1).getSqlKeyword());
    }

    if (!(value2 instanceof Column)) {
      this.parameters.add(value2);
    } else {
      this.sqlStatement = StringUtils.replaceOnce(this.sqlStatement, " AND ?", " AND " + ((Column)value2).getSqlKeyword());
    }

    ensureNoNullParameter();
  }
}
