package com.rapleaf.jack.queries;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.rapleaf.jack.exception.JackRuntimeException;

public abstract class GenericTable<T extends GenericTable<T>> {

  public final Table<?, ?> table;
  public final Column<Long> id;
  private final Class<T> tableType;
  protected final List<Column<?>> columns;

  protected GenericTable(Table<?, ?> table, Class<T> tableType, Column<?> firstColumn, Column<?>... otherColumns) {
    this.table = table;
    this.id = Column.fromId(table.getAlias());
    this.tableType = tableType;
    this.columns = Lists.newLinkedList();
    this.columns.add(firstColumn);
    this.columns.addAll(Arrays.asList(otherColumns));
  }

  public abstract T as(String alias);

  protected Table<?, ?> getAliasTable(String alias) {
    try {
      Method method = table.getClass().getMethod("as", String.class);
      return (Table<?, ?>)method.invoke(null, alias);
    } catch (Exception e) {
      throw new JackRuntimeException(e);
    }
  }

  public static abstract class Builder<T extends GenericTable<T>> {
    protected final Table<?, ?> table;
    protected final Set<String> allColumns;

    protected Builder(Table<?, ?> table) {
      this.table = table;
      this.allColumns = table.getAllColumns().stream()
          .map(Column::getField)
          .filter(Objects::nonNull)
          .map(Enum::name)
          .collect(Collectors.toSet());
    }

    protected void checkColumn(Column column) {
      String columnName = Preconditions.checkNotNull(column.getField(), "Invalid column: %s", column.toString()).name();
      Preconditions.checkArgument(column.getTable().equals(table.getAlias()) && allColumns.contains(columnName), "Column %s does not belong to table %s", columnName, table.getName());
    }

    public abstract T create();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() +
        "{" +
        table.getAlias() + ": " +
        Joiner.on(", ").join(columns) +
        "}";
  }

  @Override
  public int hashCode() {
    int hashCode = table.getSqlKeyword().hashCode();
    for (Column column : columns) {
      hashCode += 19 * column.getSqlKeyword().hashCode();
    }
    return hashCode;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other == null) {
      return false;
    }

    if (!(other.getClass().equals(tableType))) {
      return false;
    }

    T that = tableType.cast(other);
    return Objects.equals(this.table, that.table) && Objects.equals(this.columns, that.columns);
  }

}
