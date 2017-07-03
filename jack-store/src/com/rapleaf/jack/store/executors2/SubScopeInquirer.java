package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.GenericConstraint;
import com.rapleaf.jack.queries.GenericQuery;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbConstants;

public class SubScopeInquirer extends BaseInquirerExecutor2<JsRecords, SubScopeInquirer> {

  private final List<GenericConstraint> scopeConstraints = Lists.newArrayList();
  private final Map<String, List<GenericConstraint>> recordConstraints = Maps.newHashMap();

  SubScopeInquirer(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  public SubScopeInquirer whereSubScopeId(IWhereOperator<Long> scopeIdConstraint) {
    this.scopeConstraints.add(new GenericConstraint<>(table.idColumn, scopeIdConstraint));
    return this;
  }

  public SubScopeInquirer whereSubScopeName(IWhereOperator<String> scopeNameConstraint) {
    this.scopeConstraints.add(new GenericConstraint<>(table.valueColumn, scopeNameConstraint));
    return this;
  }

  public SubScopeInquirer whereSubRecord(String key, IWhereOperator<String> valueConstraint) {
    GenericConstraint constraint = new GenericConstraint<>(table.valueColumn, valueConstraint);
    String queryKey = processKey(key);
    if (this.recordConstraints.containsKey(queryKey)) {
      this.recordConstraints.get(queryKey).add(constraint);
    } else {
      this.recordConstraints.put(queryKey, Lists.newArrayList(constraint));
    }
    return this;
  }

  private static String processKey(String key) {
    String[] paths = key.split(Pattern.quote(JsonDbConstants.PATH_SEPARATOR));
    if (paths.length == 1) {
      return key;
    } else {
      return Joiner.on("%.").join(paths) + "%";
    }
  }

  @Override
  public JsRecords execute(IDb db) throws IOException {
    Set<Long> validSubScopeIds = getSubScopeIds(db);
    return new SubScopeReader(table, executionScopeId).whereSubScopeIds(validSubScopeIds).execute(db);
  }

  private Set<Long> getSubScopeIds(IDb db) throws IOException {
    Set<Long> subScopeIds = querySubScopesByScopeConstraints(db);
    return querySubScopesByRecordConstraints(db, subScopeIds);
  }

  private Set<Long> querySubScopesByScopeConstraints(IDb db) throws IOException {
    GenericQuery query = db.createQuery()
        .from(table.table)
        .where(table.scopeColumn.equalTo(executionScopeId))
        .where(table.typeColumn.equalTo(ValueType.SCOPE.value))
        .where(table.keyColumn.equalTo(JsConstants.SCOPE_KEY))
        .select(table.idColumn);

    for (GenericConstraint constraint : scopeConstraints) {
      query.where(constraint);
    }

    return Sets.newHashSet(query.fetch().gets(table.idColumn));
  }

  private Set<Long> querySubScopesByRecordConstraints(IDb db, Set<Long> subScopeIds) throws IOException {
    if (subScopeIds.isEmpty() || recordConstraints.isEmpty()) {
      return subScopeIds;
    }

    Set<Long> scopeIds = Sets.newHashSet(subScopeIds);
    for (Map.Entry<String, List<GenericConstraint>> entry : recordConstraints.entrySet()) {
      if (scopeIds.isEmpty()) {
        return Collections.emptySet();
      }

      String key = entry.getKey();
      List<GenericConstraint> constraints = entry.getValue();

      GenericQuery query = db.createQuery()
          .from(table.table)
          .where(table.scopeColumn.in(scopeIds))
          .select(table.scopeColumn);

      if (key.contains("%")) {
        query.where(table.keyColumn.matches(key));
      } else {
        query.where(table.keyColumn.equalTo(key));
      }

      for (GenericConstraint constraint : constraints) {
        query.where(constraint);
      }

      scopeIds = Sets.newHashSet(query.fetch().gets(table.scopeColumn));
    }

    return scopeIds;
  }

  @Override
  SubScopeInquirer getSelf() {
    return this;
  }

}
