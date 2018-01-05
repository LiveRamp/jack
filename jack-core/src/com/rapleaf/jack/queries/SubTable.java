package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

public class SubTable implements Table {
  private static final String ALIAS_PREFIX = "t";

  private final GenericQuery subQuery;
  private final String alias;

  SubTable(GenericQuery subQuery, String alias) {
    this.subQuery = subQuery;
    this.alias = ALIAS_PREFIX + alias;
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
          columns.add(column.asIn(alias));
        }
      }
    } else {
      for (Column column : subQuery.getSelectedColumns()) {
        columns.add(column.asIn(alias));
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
    return column.asIn(alias);
  }

  public Collection<Column> columns(Collection<Column> columns) {
    return columns.stream().map(c -> c.asIn(alias)).collect(Collectors.toSet());
  }

  public Collection<Column> columns(Column column, Column... columns) {
    Set<Column> aliasedColumns = Sets.newHashSetWithExpectedSize(1 + columns.length);
    aliasedColumns.add(column.asIn(alias));
    aliasedColumns.addAll(Arrays.stream(columns).map(c -> c.asIn(alias)).collect(Collectors.toSet()));
    return aliasedColumns;
  }
}
