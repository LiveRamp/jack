package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Collection;
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
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.json.JsonDbConstants;

public class SubScopeInquirer extends BaseInquirerExecutor2<JsRecords, SubScopeInquirer> {

  /**
   * This is only used by internal users, (e.g. {@link SubScopeUpdater}), that performs query with valid sub scope IDs.
   */
  private final Set<Long> subScopeIds = Sets.newHashSet();
  private final List<GenericConstraint> scopeConstraints = Lists.newArrayList();
  private final Map<String, List<GenericConstraint>> recordConstraints = Maps.newHashMap();
  private boolean ignoreInvalidSubScopes = false;

  SubScopeInquirer(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  SubScopeInquirer(JsTable table, Long executionScopeId, Collection<Long> subScopeIds) {
    super(table, executionScopeId);
    this.subScopeIds.addAll(subScopeIds);
  }

  @Override
  SubScopeInquirer getSelf() {
    return this;
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

  public SubScopeInquirer ignoreInvalidSubScopes() {
    this.ignoreInvalidSubScopes = true;
    return this;
  }

  @Override
  public JsRecords execute(IDb db) throws IOException {
    return null;
  }

  private static String processKey(String key) {
    String[] paths = key.split(Pattern.quote(JsonDbConstants.PATH_SEPARATOR));
    if (paths.length == 1) {
      return key;
    } else {
      return Joiner.on("%.").join(paths) + "%";
    }
  }

}
