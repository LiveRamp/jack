package com.rapleaf.jack.store.executors;

import java.io.IOException;
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
import com.rapleaf.jack.store.exceptions.InvalidRecordException;

final class InternalScopeGetter {

  private InternalScopeGetter() {
  }

  /**
   * - When subRecordIds is not present, get all sub scope IDs under execution scope;
   * - When subRecordIds is present:
   * 1) When subRecordIds is empty, return empty set
   * 2) When subRecordIds is not empty, validate sub scope IDs
   */
  static Set<Long> getValidSubRecordIds(IDb db, JsTable table, Long executionRecordId, Optional<Set<Long>> subRecordIds) throws IOException {
    Set<Long> validSubRecordIds;
    if (subRecordIds.isPresent()) {
      if (subRecordIds.get().isEmpty()) {
        return Collections.emptySet();
      }
      validateSubRecordIds(db, table, executionRecordId, subRecordIds.get());
      validSubRecordIds = subRecordIds.get();
    } else {
      validSubRecordIds = getAllSubRecordIds(db, table, executionRecordId);
    }

    return validSubRecordIds;
  }

  static Set<Long> getAllSubRecordIds(IDb db, JsTable table, Long executionRecordId) throws IOException {
    return Sets.newHashSet(
        db.createQuery().from(table.table)
            .where(table.scopeColumn.equalTo(executionRecordId))
            .where(table.typeColumn.equalTo(ValueType.SCOPE.value))
            .select(table.idColumn)
            .fetch()
            .gets(table.idColumn)
    );
  }

  static void validateSubRecordIds(IDb db, JsTable table, Long executionRecordId, Set<Long> subRecordIds) throws IOException {
    Set<Long> validSubRecordIds = Sets.newHashSet(
        db.createQuery().from(table.table)
            .where(table.scopeColumn.equalTo(executionRecordId))
            .where(table.typeColumn.equalTo(ValueType.SCOPE.value))
            .where(table.idColumn.in(subRecordIds))
            .select(table.idColumn)
            .fetch()
            .gets(table.idColumn)
    );
    if (!validSubRecordIds.equals(subRecordIds)) {
      throw new InvalidRecordException(String.format(
          "Sub scopes %s does not exist under scope %s; either ignore invalid sub scopes or provide valid sub scope IDs",
          Joiner.on(", ").join(Sets.difference(subRecordIds, validSubRecordIds)), executionRecordId == null ? JsConstants.ROOT_RECORD_NAME : executionRecordId
      ));
    }
  }

  static Set<Long> getNestedRecordIds(IDb db, JsTable table, Set<Long> recordIds) throws IOException {
    Set<Long> allNestedRecordIds = Sets.newHashSet();

    Set<Long> ids = Sets.newHashSet(recordIds);
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
          query.where(table.typeColumn.equalTo(ValueType.SCOPE.value))
              .select(table.idColumn)
              .fetch()
              .gets(table.idColumn)
      );
      allNestedRecordIds.addAll(nestedIds);
      ids = nestedIds;
    }

    return allNestedRecordIds;
  }

}
