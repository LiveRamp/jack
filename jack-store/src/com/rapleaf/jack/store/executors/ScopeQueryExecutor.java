package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.GenericConstraint;
import com.rapleaf.jack.queries.LimitCriterion;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsScopes;
import com.rapleaf.jack.store.exceptions.MissingScopeException;
import com.rapleaf.jack.transaction.ITransactor;

public class ScopeQueryExecutor<DB extends IDb> extends BaseExecutor<DB> {

  private final List<GenericConstraint> scopeConstraints;
  private final Map<Column, QueryOrder> orderCriteria;
  private Optional<LimitCriterion> limitCriteria;

  ScopeQueryExecutor(ITransactor<DB> transactor, JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(transactor, table, predefinedScope, predefinedScopeNames);
    this.scopeConstraints = Lists.newArrayList();
    this.orderCriteria = Maps.newHashMapWithExpectedSize(2);
    this.limitCriteria = Optional.empty();
  }

  public ScopeQueryExecutor<DB> whereScopeId(IWhereOperator<Long> scopeIdConstraint) {
    this.scopeConstraints.add(new GenericConstraint<>(table.idColumn.as(Long.class), scopeIdConstraint));
    return this;
  }

  public ScopeQueryExecutor<DB> whereScopeName(IWhereOperator<String> scopeNameConstraint) {
    this.scopeConstraints.add(new GenericConstraint<>(table.valueColumn, scopeNameConstraint));
    return this;
  }

  public ScopeQueryExecutor<DB> orderByScopeId(QueryOrder queryOrder) {
    this.orderCriteria.put(table.idColumn, queryOrder);
    return this;
  }

  public ScopeQueryExecutor<DB> orderByScopeName(QueryOrder queryOrder) {
    this.orderCriteria.put(table.valueColumn, queryOrder);
    return this;
  }

  public ScopeQueryExecutor<DB> limit(int limit) {
    this.limitCriteria = Optional.of(new LimitCriterion(limit));
    return this;
  }

  public ScopeQueryExecutor<DB> limit(int offset, int limit) {
    this.limitCriteria = Optional.of(new LimitCriterion(offset, limit));
    return this;
  }

  public JsScopes fetch() {
    Optional<JsScope> executionScope = getExecutionScope();
    if (executionScope.isPresent()) {
      return queryScope(executionScope.get(), scopeConstraints, orderCriteria, limitCriteria);
    } else {
      throw new MissingScopeException(Joiner.on("/").join(predefinedScopeNames));
    }
  }

}
