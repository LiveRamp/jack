package com.rapleaf.jack.store;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.Table;

public class JsTable {
  public final Table<?, ?> table;
  public final Column<Long> idColumn;
  public final Column<Long> scopeColumn;
  public final Column<String> typeColumn;
  public final Column<String> keyColumn;
  public final Column<String> valueColumn;

  private JsTable(Table<?, ?> table, Column<Long> scopeColumn, Column<String> typeColumn, Column<String> keyColumn, Column<String> valueColumn) {
    this.table = table;
    this.idColumn = Column.fromId(table.getName());
    this.scopeColumn = scopeColumn;
    this.typeColumn = typeColumn;
    this.keyColumn = keyColumn;
    this.valueColumn = valueColumn;
  }

  public static Builder from(Table<?, ?> table) {
    return new Builder(table);
  }

  public static class Builder {
    private final Table<?, ?> table;
    private final Set<String> allColumns;

    private Column<Long> scopeColumn;
    private Column<String> typeColumn;
    private Column<String> keyColumn;
    private Column<String> valueColumn;

    private Builder(Table<?, ?> table) {
      this.table = table;
      this.allColumns = table.getAllColumns().stream()
          .map(Column::getField)
          .filter(Objects::nonNull)
          .map(Enum::name)
          .collect(Collectors.toSet());
    }

    private void checkColumn(Column column) {
      String columnName = Preconditions.checkNotNull(column.getField(), "Invalid column: %s", column.toString()).name();
      Preconditions.checkArgument(column.getTable().equals(table.getName()) && allColumns.contains(columnName), "Column %s does not belong to table %s", columnName, table.getName());
    }

    public Builder setScopeColumn(Column<Long> scopeColumn) {
      checkColumn(scopeColumn);
      this.scopeColumn = scopeColumn;
      return this;
    }

    public Builder setTypeColumn(Column<String> typeColumn) {
      checkColumn(typeColumn);
      this.typeColumn = typeColumn;
      return this;
    }

    public Builder setKeyColumn(Column<String> keyColumn) {
      checkColumn(keyColumn);
      this.keyColumn = keyColumn;
      return this;
    }

    public Builder setValueColumn(Column<String> valueColumn) {
      checkColumn(valueColumn);
      this.valueColumn = valueColumn;
      return this;
    }

    public JsTable create() {
      if (scopeColumn == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.scope.name()));
        scopeColumn = Column.fromField(table.getName(), JsConstants.DefaultTableField.scope, Long.class);
      }
      if (typeColumn == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.type.name()));
        typeColumn = Column.fromField(table.getName(), JsConstants.DefaultTableField.type, String.class);
      }
      if (keyColumn == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.key.name()));
        keyColumn = Column.fromField(table.getName(), JsConstants.DefaultTableField.key, String.class);
      }
      if (valueColumn == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.value.name()));
        valueColumn = Column.fromField(table.getName(), JsConstants.DefaultTableField.value, String.class);
      }
      return new JsTable(table, scopeColumn, typeColumn, keyColumn, valueColumn);
    }
  }
}
