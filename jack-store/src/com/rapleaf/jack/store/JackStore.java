package com.rapleaf.jack.store;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.Table;
import com.rapleaf.jack.store.executors.JsBaseExecutor;
import com.rapleaf.jack.store.executors.JsExecutors;
import com.rapleaf.jack.transaction.ITransactor;

public class JackStore<DB extends IDb> {

  private final JsBaseExecutor<DB> baseExecutor;

  public JackStore(ITransactor<DB> transactor, Table<?, ?> table, Column<String> scopeColumn, Column<String> typeColumn, Column<String> keyColumn, Column<String> valueColumn) {
    this.baseExecutor = new JsBaseExecutor<>(transactor, table, scopeColumn, typeColumn, keyColumn, valueColumn, JsConstants.ROOT_SCOPE);
  }

  public JsExecutors<DB> within(String scope, String... moreScopes) {
    List<String> scopes = Lists.newArrayListWithCapacity(1 + moreScopes.length);
    scopes.add(scope);
    scopes.addAll(Arrays.asList(moreScopes));
    return within(scopes);
  }

  public JsExecutors<DB> within(List<String> scopes) {
    Preconditions.checkArgument(scopes.size() > 0, "Scope list cannot be empty; to specify root scope, please use the `withinRoot` method");
    Preconditions.checkArgument(scopes.stream().noneMatch(String::isEmpty), "Scope name cannot be empty");

    return within(baseExecutor.getOrCreateScope(scopes));
  }

  public JsExecutors<DB> within(JsScope scope) {
    return new JsExecutors<>(baseExecutor, scope);
  }

  public JsExecutors<DB> withinRoot() {
    return within(JsConstants.ROOT_SCOPE);
  }
}
