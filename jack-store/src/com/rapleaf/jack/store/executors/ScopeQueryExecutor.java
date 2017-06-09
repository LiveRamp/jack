package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.GenericConstraint;
import com.rapleaf.jack.queries.GenericQuery;
import com.rapleaf.jack.queries.LimitCriterion;
import com.rapleaf.jack.queries.OrderCriterion;
import com.rapleaf.jack.queries.QueryOrder;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsScopes;

public class ScopeQueryExecutor<DB extends IDb> {

  private final JsBaseExecutor<DB> baseExecutor;
  private final JsScope executionScope;
  private final List<GenericConstraint> scopeConstraints;
  private final Map<Column, QueryOrder> orderCriteria;
  private Optional<LimitCriterion> limitCriteria;

  public ScopeQueryExecutor(JsBaseExecutor<DB> baseExecutor, JsScope executionScope) {
    this.baseExecutor = baseExecutor;
    this.executionScope = executionScope;
    this.scopeConstraints = Lists.newArrayList();
    this.orderCriteria = Maps.newHashMapWithExpectedSize(2);
    this.limitCriteria = Optional.empty();
  }

  public ScopeQueryExecutor<DB> whereScope(IWhereOperator<String> scopeConstraint) {
    this.scopeConstraints.add(new GenericConstraint<>(baseExecutor.valueColumn, scopeConstraint));
    return this;
  }

  public ScopeQueryExecutor<DB> orderByScopeName(QueryOrder queryOrder) {
    this.orderCriteria.put(baseExecutor.valueColumn, queryOrder);
    return this;
  }

  public ScopeQueryExecutor<DB> orderByScopeId(QueryOrder queryOrder) {
    this.orderCriteria.put(baseExecutor.idColumn, queryOrder);
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
    return baseExecutor.transactor.queryAsTransaction(db -> {
      GenericQuery query = db.createQuery()
          .from(baseExecutor.table)
          .where(baseExecutor.scopeColumn.as(Long.class).equalTo(executionScope.getScopeId()))
          .where(baseExecutor.typeColumn.equalTo(JsConstants.SCOPE_TYPE))
          .where(baseExecutor.keyColumn.equalTo(JsConstants.SCOPE_KEY))
          .select(baseExecutor.idColumn, baseExecutor.valueColumn);

      for (GenericConstraint constraint : scopeConstraints) {
        query.where(constraint);
      }

      for (Map.Entry<Column, QueryOrder> entry : orderCriteria.entrySet()) {
        query.orderBy(entry.getKey(), entry.getValue());
      }

      if (limitCriteria.isPresent()) {
        LimitCriterion limit = limitCriteria.get();
        query.limit(limit.getOffset(), limit.getNResults());
      }

      List<JsScope> scopes = query.fetch().stream()
          .map(r -> new JsScope(r.get(baseExecutor.idColumn), r.get(baseExecutor.valueColumn)))
          .collect(Collectors.toList());

      return new JsScopes(scopes);
    });
  }

}
