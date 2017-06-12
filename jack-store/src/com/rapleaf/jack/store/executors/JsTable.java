package com.rapleaf.jack.store.executors;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.Table;

public class JsTable {
  final Table<?, ?> table;
  final Column<Long> idColumn;
  final Column<String> scopeColumn;
  final Column<String> typeColumn;
  final Column<String> keyColumn;
  final Column<String> valueColumn;

  public JsTable(Table<?, ?> table, Column<String> scopeColumn, Column<String> typeColumn, Column<String> keyColumn, Column<String> valueColumn) {
    this.table = table;
    this.idColumn = Column.fromId(table.getName());
    this.scopeColumn = scopeColumn;
    this.typeColumn = typeColumn;
    this.keyColumn = keyColumn;
    this.valueColumn = valueColumn;
  }
}
