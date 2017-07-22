package com.rapleaf.jack.store;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.GenericTable;
import com.rapleaf.jack.queries.Table;

public class JsTable extends GenericTable<JsTable> {
  public final Table<?, ?> table;
  public final Column<Long> scope;
  public final Column<Integer> type;
  public final Column<String> key;
  public final Column<String> value;

  private JsTable(Table<?, ?> table, Column<Long> scope, Column<Integer> type, Column<String> key, Column<String> value) {
    super(table, JsTable.class, scope, type, key, value);
    this.table = table;
    this.scope = scope;
    this.type = type;
    this.key = key;
    this.value = value;
  }

  @Override
  public JsTable as(String alias) {
    return JsTable.from(getAliasTable(alias)).create();
  }

  public static Builder from(Table<?, ?> table) {
    return new Builder(table);
  }

  public static class Builder extends GenericTable.Builder<JsTable> {
    private Column<Long> scope;
    private Column<Integer> type;
    private Column<String> key;
    private Column<String> value;

    private Builder(Table<?, ?> table) {
      super(table);
    }

    public Builder setScopeColumn(Column<Long> scopeColumn) {
      checkColumn(scopeColumn);
      this.scope = scopeColumn;
      return this;
    }

    public Builder setTypeColumn(Column<Integer> typeColumn) {
      checkColumn(typeColumn);
      this.type = typeColumn;
      return this;
    }

    public Builder setKeyColumn(Column<String> keyColumn) {
      checkColumn(keyColumn);
      this.key = keyColumn;
      return this;
    }

    public Builder setValueColumn(Column<String> valueColumn) {
      checkColumn(valueColumn);
      this.value = valueColumn;
      return this;
    }

    @Override
    public JsTable create() {
      if (scope == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.entry_scope.name()));
        scope = Column.fromField(table.getAlias(), JsConstants.DefaultTableField.entry_scope, Long.class);
      }
      if (type == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.entry_type.name()));
        type = Column.fromField(table.getAlias(), JsConstants.DefaultTableField.entry_type, Integer.class);
      }
      if (key == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.entry_key.name()));
        key = Column.fromField(table.getAlias(), JsConstants.DefaultTableField.entry_key, String.class);
      }
      if (value == null) {
        Preconditions.checkArgument(allColumns.contains(JsConstants.DefaultTableField.entry_value.name()));
        value = Column.fromField(table.getAlias(), JsConstants.DefaultTableField.entry_value, String.class);
      }
      return new JsTable(table, scope, type, key, value);
    }
  }

}
