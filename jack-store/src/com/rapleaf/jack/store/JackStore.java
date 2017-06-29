package com.rapleaf.jack.store;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.Table;
import com.rapleaf.jack.store.executors.JsExecutors;
import com.rapleaf.jack.store.executors.JsTable;
import com.rapleaf.jack.transaction.ITransactor;

public class JackStore<DB extends IDb> {

  private final ITransactor<DB> transactor;
  private final JsTable jsTable;

  public JackStore(ITransactor<DB> transactor, Table<?, ?> table, Column<Long> scopeColumn, Column<String> typeColumn, Column<String> keyColumn, Column<String> valueColumn) {
    this.transactor = transactor;
    this.jsTable = new JsTable(table, scopeColumn, typeColumn, keyColumn, valueColumn);
  }

  public JsExecutors<DB> scope(String scope, String... moreScopes) {
    List<String> scopes = Lists.newArrayListWithCapacity(1 + moreScopes.length);
    scopes.add(scope);
    scopes.addAll(Arrays.asList(moreScopes));
    return scope(scopes);
  }

  public JsExecutors<DB> scope(List<String> scopes) {
    Preconditions.checkArgument(scopes.size() > 0, "Scope list cannot be empty; to specify root scope, please use the `rootScope` method");
    Preconditions.checkArgument(scopes.stream().noneMatch(String::isEmpty), "Scope name cannot be empty");
    return new JsExecutors<>(transactor, jsTable, scopes);
  }

  public JsExecutors<DB> scope(JsScope scope) {
    return new JsExecutors<>(transactor, jsTable, scope);
  }

  public JsExecutors<DB> rootScope() {
    return scope(JsConstants.ROOT_SCOPE);
  }
}
