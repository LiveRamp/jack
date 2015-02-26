package com.rapleaf.jack.queries;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.rapleaf.jack.BaseDatabaseConnection;
import com.rapleaf.jack.Column;
import com.rapleaf.jack.Table;

public class GenericQuery {

  private final BaseDatabaseConnection dbConnection;
  private final List<Table> includedTables;
  private final List<JoinCondition> joinConditions;
  private final List<WhereConstraint> whereConstraints;
  private final List<OrderCriterion> orderCriteria;
  private final Set<Column> selectedColumns;
  private final Set<Column> groupByColumns;
  private Optional<LimitCriterion> limitCriteria;

  private GenericQuery(BaseDatabaseConnection dbConnection) {
    this.dbConnection = dbConnection;
    this.includedTables = Lists.newArrayList();
    this.joinConditions = Lists.newArrayList();
    this.whereConstraints = Lists.newArrayList();
    this.orderCriteria = Lists.newArrayList();
    this.selectedColumns = Sets.newHashSet();
    this.groupByColumns = Sets.newHashSet();
    this.limitCriteria = Optional.absent();
  }

  public static GenericQuery create(BaseDatabaseConnection dbConnection) {
    return new GenericQuery(dbConnection);
  }

  public GenericQueryBuilder from(Table table) {
    this.includedTables.add(table);
    return new GenericQueryBuilder(dbConnection, this);
  }

  void addSelectedColumns(Column column, Column... columns) {
    this.selectedColumns.add(column);
    this.selectedColumns.addAll(Arrays.asList(columns));
  }

  Set<Column> getSelectedColumns() {
    return selectedColumns;
  }

  void addJoinCondition(JoinCondition joinCondition) {
    this.includedTables.add(joinCondition.getTable());
    this.joinConditions.add(joinCondition);
  }

  void addWhereCondition(WhereConstraint whereConstraint) {
    if (whereConstraints.isEmpty()) {
      // the first WHERE constraint cannot specify a logic
      whereConstraint.setLogic(null);
    } else if (whereConstraint.getLogic() == null) {
      // any non-first WHERE constraint without a logic will default to AND
      whereConstraint.setLogic(WhereConstraint.Logic.AND);
    }
    this.whereConstraints.add(whereConstraint);
  }

  List<WhereConstraint> getWhereConstraints() {
    return whereConstraints;
  }

  void addOrderCondition(OrderCriterion orderCriterion) {
    this.orderCriteria.add(orderCriterion);
  }

  void addLimitCondition(LimitCriterion limitCriterion) {
    this.limitCriteria = Optional.of(limitCriterion);
  }

  void addGroupByColumns(Column column, Column... columns) {
    this.groupByColumns.add(column);
    this.groupByColumns.addAll(Arrays.asList(columns));
  }

  String getSqlStatement() {
    return getSelectClause()
        + getFromClause()
        + getJoinClause()
        + getWhereClause()
        + getGroupByClause()
        + getOrderClause()
        + getLimitClause();
  }

  private String getSelectClause() {
    if (!groupByColumns.isEmpty()) {
      if (selectedColumns.isEmpty()) {
        throw new RuntimeException("The SELECT list cannot be empty when the GROUP BY clause is specified.");
      }

      for (Column column : selectedColumns) {
        if (!groupByColumns.contains(column) && !(column instanceof AggregatedColumn)) {
          throw new RuntimeException("The non-aggregated column " + column.getSqlKeyword() +
              " not named in the GROUP BY clause cannot be included in the SELECT list.");
        }
      }
    }

    if (selectedColumns.isEmpty()) {
      for (Table table : includedTables) {
        selectedColumns.addAll(table.getAllColumns());
      }
    }
    return getClauseFromColumns(selectedColumns, "SELECT ", ", ");
  }

  private String getFromClause() {
    return "FROM " + includedTables.get(0).getSqlKeyword() + " ";
  }

  private String getJoinClause() {
    return getClauseFromQueryConditions(joinConditions, "", " ");
  }

  private String getWhereClause() {
    return getClauseFromQueryConditions(whereConstraints, "WHERE ", " ");
  }

  private String getGroupByClause() {
    return getClauseFromColumns(groupByColumns, "GROUP BY ", ", ");
  }

  private String getOrderClause() {
    if (orderCriteria.isEmpty()) {
      return "";
    } else {
      return getClauseFromQueryConditions(orderCriteria, "ORDER BY ", ", ");
    }
  }

  private String getLimitClause() {
    if (limitCriteria.isPresent()) {
      return limitCriteria.get().getSqlStatement() + " ";
    } else {
      return "";
    }
  }

  private String getClauseFromColumns(Collection<Column> columns, String initialKeyword, String separator) {
    if (columns.isEmpty()) {
      return "";
    }

    StringBuilder clause = new StringBuilder(initialKeyword);
    Iterator<Column> it = columns.iterator();
    while (it.hasNext()) {
      clause.append(it.next().getSqlKeyword());
      if (it.hasNext()) {
        clause.append(separator);
      }
    }

    return clause.append(" ").toString();
  }

  private <T extends QueryCondition> String getClauseFromQueryConditions(Collection<T> conditions, String initialKeyword, String separator) {
    if (conditions.isEmpty()) {
      return "";
    }

    StringBuilder clause = new StringBuilder(initialKeyword);
    Iterator<T> it = conditions.iterator();
    while (it.hasNext()) {
      clause.append(it.next().getSqlStatement());
      if (it.hasNext()) {
        clause.append(separator);
      }
    }

    return clause.append(" ").toString();
  }
}
