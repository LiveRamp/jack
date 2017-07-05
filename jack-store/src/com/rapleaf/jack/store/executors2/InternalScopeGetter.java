package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.exceptions.InvalidScopeException;
import com.rapleaf.jack.store.exceptions.MissingScopeException;

final class InternalScopeGetter {

  private InternalScopeGetter() {
  }

  static Set<Long> getValidSubScopeIds(IDb db, JsTable table, Long executionScopeId, Set<Long> subScopeIds, boolean ignoreInvalidSubScopes) throws IOException {
    Set<Long> validSubScopeIds;
    if (subScopeIds.isEmpty()) {
      validSubScopeIds = getAllSubScopeIds(db, table, executionScopeId);
    } else {
      validSubScopeIds = getValidSubScopeIds(db, table, executionScopeId, subScopeIds);
    }

    if (!subScopeIds.isEmpty() && !validSubScopeIds.equals(subScopeIds) && !ignoreInvalidSubScopes) {
      throw new InvalidScopeException(String.format(
          "Sub scopes %s does not exist under scope %s; either ignore invalid sub scopes or provide valid sub scope IDs",
          Joiner.on(", ").join(Sets.difference(subScopeIds, validSubScopeIds)), executionScopeId == null ? JsConstants.ROOT_SCOPE_NAME : executionScopeId
      ));
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

}
