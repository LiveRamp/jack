package com.rapleaf.jack.store;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.GenericTable;
import com.rapleaf.jack.queries.Table;

public class JsTable extends GenericTable<JsTable> {
  public final Table<?, ?> table;
  public final Column<Long> scopeColumn;
  public final Column<Integer> typeColumn;
  public final Column<String> keyColumn;
  public final Column<String> valueColumn;

  private JsTable(Table<?, ?> table, Column<Long> scopeColumn, Column<Integer> typeColumn, Column<String> keyColumn, Column<String> valueColumn) {
    super(table, JsTable.class, scopeColumn, typeColumn, keyColumn, valueColumn);
    this.table = table;
    this.scopeColumn = scopeColumn;
    this.typeColumn = typeColumn;
    this.keyColumn = keyColumn;
    this.valueColumn = valueColumn;
  }

  @Override
  public JsTable as(String alias) {
    return JsTable.from(getAliasTable(alias)).create();
  }

  public static Builder from(Table<?, ?> table) {
    return new Builder(table);
  }

  public static class Builder extends GenericTable.Builder<JsTable> {
    private Column<Long> scopeColumn;
    private Column<Integer> typeColumn;
    private Column<String> keyColumn;
    private Column<String> valueColumn;

    private Builder(Table<?, ?> table) {
      super(table);
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

    @Override
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

}
