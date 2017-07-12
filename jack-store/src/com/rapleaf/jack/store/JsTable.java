package com.rapleaf.jack.store;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.Table;

public class JsTable {
  public final Table<?, ?> table;
  public final Column<Long> idColumn;
  public final Column<Long> scopeColumn;
  public final Column<Integer> typeColumn;
  public final Column<String> keyColumn;
  public final Column<String> valueColumn;

  private JsTable(Table<?, ?> table, Column<Long> scopeColumn, Column<Integer> typeColumn, Column<String> keyColumn, Column<String> valueColumn) {
    this.table = table;
    this.idColumn = Column.fromId(table.getAlias());
    this.scopeColumn = scopeColumn;
    this.typeColumn = typeColumn;
    this.keyColumn = keyColumn;
    this.valueColumn = valueColumn;
  }

  public JsTable as(String alias) {
    try {
      Method method = table.getClass().getMethod("as", String.class);
      Table aliasTable = (Table)method.invoke(null, alias);
      return JsTable.from(aliasTable).create();
    } catch (Exception e) {
      throw new JackRuntimeException(e);
    }
  }

  public static Builder from(Table<?, ?> table) {
    return new Builder(table);
  }

  public static class Builder {
    private final Table<?, ?> table;
    private final Set<String> allColumns;

    private Column<Long> scopeColumn;
    private Column<Integer> typeColumn;
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
      Preconditions.checkArgument(column.getTable().equals(table.getAlias()) && allColumns.contains(columnName), "Column %s does not belong to table %s", columnName, table.getName());
    }

    public Builder setScopeColumn(Column<Long> scopeColumn) {
      checkColumn(scopeColumn);
      this.scopeColumn = scopeColumn;
      return this;
    }

    public Builder setTypeColumn(Column<Integer> typeColumn) {
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
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.entry_scope.name()));
        scopeColumn = Column.fromField(table.getAlias(), JsConstants.DefaultTableField.entry_scope, Long.class);
      }
      if (typeColumn == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.entry_type.name()));
        typeColumn = Column.fromField(table.getAlias(), JsConstants.DefaultTableField.entry_type, Integer.class);
      }
      if (keyColumn == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.entry_key.name()));
        keyColumn = Column.fromField(table.getAlias(), JsConstants.DefaultTableField.entry_key, String.class);
      }
      if (valueColumn == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.entry_value.name()));
        valueColumn = Column.fromField(table.getAlias(), JsConstants.DefaultTableField.entry_value, String.class);
      }
      return new JsTable(table, scopeColumn, typeColumn, keyColumn, valueColumn);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() +
        "{" +
        "table: " + table.getSqlKeyword() +
        ", idColumn: " + idColumn +
        ", scopeColumn: " + scopeColumn +
        ", typeColumn: " + typeColumn +
        ", keyColumn: " + keyColumn +
        ", valueColumn: " + valueColumn +
        "}";
  }

  @Override
  public int hashCode() {
    int hashCode = table.getSqlKeyword().hashCode();
    hashCode += 19 * idColumn.getSqlKeyword().hashCode();
    hashCode += 19 * scopeColumn.getSqlKeyword().hashCode();
    hashCode += 19 * typeColumn.getSqlKeyword().hashCode();
    hashCode += 19 * keyColumn.getSqlKeyword().hashCode();
    hashCode += 19 * valueColumn.getSqlKeyword().hashCode();
    return hashCode;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (!(other instanceof JsTable)) {
      return false;
    }

    JsTable that = (JsTable)other;
    return Objects.equals(this.table, that.table) &&
        Objects.equals(this.idColumn, that.idColumn) &&
        Objects.equals(this.scopeColumn, that.scopeColumn) &&
        Objects.equals(this.typeColumn, that.typeColumn) &&
        Objects.equals(this.keyColumn, that.keyColumn) &&
        Objects.equals(this.valueColumn, that.valueColumn);
  }

}
