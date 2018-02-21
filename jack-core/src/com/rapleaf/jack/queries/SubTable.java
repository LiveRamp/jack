package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import com.rapleaf.jack.AttributesWithId;
import com.rapleaf.jack.ModelWithId;

public class SubTable implements Table {
  private final GenericQuery subQuery;
  private final String alias;

  SubTable(GenericQuery subQuery, String alias) {
    this.subQuery = subQuery;
    this.alias = alias;
  }

  @Override
  public String getName() {
    return alias;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public Set<Column> getAllColumns() {
    Set<Column> columns = Sets.newHashSet();

    if (subQuery.getSelectedColumns().isEmpty()) {
      for (Table table : subQuery.getIncludedTables()) {
        for (Column column : table.getAllColumns()) {
          columns.add(column.forTable(alias));
        }
      }
    } else {
      for (Column column : subQuery.getSelectedColumns()) {
        columns.add(column.forTable(alias));
      }
    }

    return columns;
  }

  @Override
  public String getSqlKeyword() {
    return String.format("(%s) as %s", subQuery.getQueryStatement(), alias);
  }

  @Override
  public List<?> getParameters() {
    return subQuery.getParameters();
  }

  public <T> Column<T> column(Column<T> column) {
    return column.forTable(alias);
  }

  public Collection<Column> columns(Collection<Column> columns) {
    return columns.stream().map(c -> c.forTable(alias)).collect(Collectors.toSet());
  }

  public Collection<Column> columns(Column column, Column... columns) {
    Set<Column> aliasedColumns = Sets.newHashSetWithExpectedSize(1 + columns.length);
    aliasedColumns.add(column.forTable(alias));
    aliasedColumns.addAll(Arrays.stream(columns).map(c -> c.forTable(alias)).collect(Collectors.toSet()));
    return aliasedColumns;
  }

  public <A extends AttributesWithId, M extends ModelWithId> AbstractTable<A, M> model(AbstractTable<A, M> table) {
    return table.alias(alias);
  }
}
