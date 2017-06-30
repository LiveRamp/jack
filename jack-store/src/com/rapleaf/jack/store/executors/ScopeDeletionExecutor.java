package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.queries.Deletions;
import com.rapleaf.jack.queries.GenericConstraint;
import com.rapleaf.jack.queries.where_operators.IWhereOperator;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsScopes;
import com.rapleaf.jack.store.JsTable;

public class ScopeDeletionExecutor<DB extends IDb> extends BaseExecutor<DB> {

  private final List<GenericConstraint> scopeConstraints;
  private boolean allowRecursion;
  private boolean allowBulkDeletion;

  ScopeDeletionExecutor(JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    super(table, predefinedScope, predefinedScopeNames);
    this.scopeConstraints = Lists.newArrayList();
    this.allowRecursion = false;
  }

  public ScopeDeletionExecutor<DB> whereScope(IWhereOperator<String> scopeNameConstraint) {
    this.scopeConstraints.add(new GenericConstraint<>(table.valueColumn, scopeNameConstraint));
    return this;
  }

  public ScopeDeletionExecutor<DB> allowBulk() {
    this.allowBulkDeletion = true;
    return this;
  }

  public ScopeDeletionExecutor<DB> allowRecursion() {
    this.allowRecursion = true;
    return this;
  }

  public boolean execute(DB db) throws IOException {
    Optional<JsScope> executionScope = getOptionalExecutionScope(db);
    if (!executionScope.isPresent()) {
      return true;
    }
    if (!allowBulkDeletion && scopeConstraints.isEmpty()) {
      throw new JackRuntimeException("Bulk deletion is disabled; either enable it or specify at least one constraint");
    }

    JsScopes scopesToDelete = ScopeQueryExecutor.queryScope(db, table, executionScope.get(), scopeConstraints);
    Set<Long> idsToDelete = Sets.newHashSet(scopesToDelete.getScopeIds());

    Set<Long> nestedScopeIds = getNestedScopeIds(db, idsToDelete);
    if (!allowRecursion && !nestedScopeIds.isEmpty()) {
      throw new JackRuntimeException("There are nested scopes under the scopes to delete");
    }
    idsToDelete.addAll(nestedScopeIds);
    return deleteScopes(db, idsToDelete);
  }

  private Set<Long> getNestedScopeIds(DB db, Set<Long> scopeIds) throws IOException {
    Set<Long> allNestedScopeIds = Sets.newHashSet();

    Set<Long> ids = Sets.newHashSet(scopeIds);
    Set<Long> nestedIds;
    while (!ids.isEmpty()) {
      nestedIds = Sets.newHashSet(
          db.createQuery().from(table.table)
              .where(table.scopeColumn.in(ids))
              .where(table.keyColumn.equalTo(JsConstants.SCOPE_KEY))
              .select(table.idColumn)
              .fetch()
              .gets(table.idColumn)
      );
      allNestedScopeIds.addAll(nestedIds);
      ids = nestedIds;
    }

    return allNestedScopeIds;
  }

  private boolean deleteScopes(DB db, Set<Long> scopeIds) throws IOException {
    // delete records
    db.createDeletion().from(table.table)
        .where(table.scopeColumn.in(scopeIds))
        .where(table.typeColumn.notEqualTo(JsConstants.SCOPE_TYPE))
        .where(table.keyColumn.notEqualTo(JsConstants.SCOPE_KEY))
        .execute();

    // delete scopes
    Deletions deletions = db.createDeletion().from(table.table)
        .where(table.idColumn.as(Long.class).in(scopeIds))
        .where(table.typeColumn.equalTo(JsConstants.SCOPE_TYPE))
        .where(table.keyColumn.equalTo(JsConstants.SCOPE_KEY))
        .execute();

    return deletions.getDeletedRowCount() == scopeIds.size();
  }

}
