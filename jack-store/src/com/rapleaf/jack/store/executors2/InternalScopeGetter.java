package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.GenericQuery;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.exceptions.InvalidScopeException;

final class InternalScopeGetter {

  private InternalScopeGetter() {
  }

  /**
   * - When subScopeIds is not present, get all sub scope IDs under execution scope;
   * - When subScopeIds is present:
   * 1) When subScopeIds is empty, return empty set
   * 2) When subScopeIds is not empty, validate sub scope IDs
   */
  static Set<Long> getValidSubScopeIds(IDb db, JsTable table, Long executionScopeId, Optional<Set<Long>> subScopeIds, boolean ignoreInvalidSubScopes) throws IOException {
    Set<Long> validSubScopeIds;
    if (subScopeIds.isPresent()) {
      if (subScopeIds.get().isEmpty()) {
        return Collections.emptySet();
      }
      validSubScopeIds = getValidSubScopeIds(db, table, executionScopeId, subScopeIds.get());
      if (!validSubScopeIds.equals(subScopeIds.get()) && !ignoreInvalidSubScopes) {
        throw new InvalidScopeException(String.format(
            "Sub scopes %s does not exist under scope %s; either ignore invalid sub scopes or provide valid sub scope IDs",
            Joiner.on(", ").join(Sets.difference(subScopeIds.get(), validSubScopeIds)), executionScopeId == null ? JsConstants.ROOT_SCOPE_NAME : executionScopeId
        ));
      }
    } else {
      validSubScopeIds = getAllSubScopeIds(db, table, executionScopeId);
    }

    return validSubScopeIds;
  }

  static Set<Long> getAllSubScopeIds(IDb db, JsTable table, Long executionScopeId) throws IOException {
    return Sets.newHashSet(
        db.createQuery().from(table.table)
            .where(table.scopeColumn.equalTo(executionScopeId))
            .where(table.typeColumn.equalTo(ValueType.SCOPE.value))
            .select(table.idColumn)
            .fetch()
            .gets(table.idColumn)
    );
  }

  static Set<Long> getValidSubScopeIds(IDb db, JsTable table, Long executionScopeId, Collection<Long> subScopeIds) throws IOException {
    return Sets.newHashSet(
        db.createQuery().from(table.table)
            .where(table.scopeColumn.equalTo(executionScopeId))
            .where(table.typeColumn.equalTo(ValueType.SCOPE.value))
            .where(table.idColumn.in(subScopeIds))
            .select(table.idColumn)
            .fetch()
            .gets(table.idColumn)
    );
  }

  static Set<Long> getNestedScopeIds(IDb db, JsTable table, Set<Long> scopeIds) throws IOException {
    Set<Long> allNestedScopeIds = Sets.newHashSet();

    Set<Long> ids = Sets.newHashSet(scopeIds);
    Set<Long> nestedIds;
    while (!ids.isEmpty()) {
      GenericQuery query = db.createQuery().from(table.table);
      if (ids.contains(null)) {
        Set<Long> nonNullIds = ids.stream().filter(Objects::nonNull).collect(Collectors.toSet());
        query.where(table.scopeColumn.in(nonNullIds).or(table.scopeColumn.isNull()));
      } else {
        query.where(table.scopeColumn.in(ids));
      }
      nestedIds = Sets.newHashSet(
          query.where(table.keyColumn.equalTo(JsConstants.SCOPE_KEY))
              .select(table.idColumn)
              .fetch()
              .gets(table.idColumn)
      );
      allNestedScopeIds.addAll(nestedIds);
      ids = nestedIds;
    }

    return allNestedScopeIds;
  }

}
