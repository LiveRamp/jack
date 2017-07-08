package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.BulkOperationException;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbConstants;

public class SubScopeDeleter extends BaseDeleterExecutor<Void, SubScopeDeleter> {

  private Optional<Set<Long>> subScopeIds = Optional.empty();
  private boolean allowBulkDeletion;

  SubScopeDeleter(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
    this.allowBulkDeletion = false;
  }

  public SubScopeDeleter whereSubScopeIds(Set<Long> subScopeIds) {
    if (this.subScopeIds.isPresent()) {
      this.subScopeIds.get().addAll(subScopeIds);
    } else {
      this.subScopeIds = Optional.of(Sets.newHashSet(subScopeIds));
    }
    return getSelf();
  }

  public SubScopeDeleter whereSubScopeIds(Long subScopeId, Long... moreSubScopeIds) {
    if (this.subScopeIds.isPresent()) {
      this.subScopeIds.get().add(subScopeId);
      this.subScopeIds.get().addAll(Arrays.asList(moreSubScopeIds));
    } else {
      Set<Long> subScopeIds = Sets.newHashSet(subScopeId);
      subScopeIds.addAll(Arrays.asList(moreSubScopeIds));
      this.subScopeIds = Optional.of(subScopeIds);
    }
    return getSelf();
  }

  public SubScopeDeleter allowBulkDeletion() {
    this.allowBulkDeletion = true;
    return getSelf();
  }

  Void internalExecute(IDb db) throws IOException {
    if (!allowBulkDeletion && !subScopeIds.isPresent()) {
      throw new BulkOperationException("Bulk deletion is disabled; either enable it or specify at least one sub scope ID");
    }

    Set<Long> validSubScopeIds = InternalScopeGetter.getValidSubScopeIds(db, table, executionScopeId, subScopeIds);
    if (deleteEntireRecord) {
      executeRecordDeletion(db, validSubScopeIds);
    } else {
      executeKeyDeletion(db, validSubScopeIds);
    }
    return null;
  }

  private void executeRecordDeletion(IDb db, Set<Long> subScopesToDelete) throws IOException {
    Set<Long> nestedScopeIds = InternalScopeGetter.getNestedScopeIds(db, table, subScopesToDelete);
    if (!nestedScopeIds.isEmpty() && !allowRecursion) {
      throw new JackRuntimeException("There are nested scopes under the scopes to delete");
    }
    Set<Long> scopeIdsToDelete = Sets.newHashSet(subScopesToDelete);
    scopeIdsToDelete.addAll(nestedScopeIds);
    deleteScopes(db, scopeIdsToDelete);
  }

  private void executeKeyDeletion(IDb db, Set<Long> subScopesToDelete) throws IOException {
    if (deleteAllKeys) {
      db.createDeletion()
          .from(table.table)
          .where(table.scopeColumn.in(subScopesToDelete))
          .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
          .execute();
    } else if (!keysToDelete.isEmpty()) {
      for (String key : keysToDelete) {
        db.createDeletion()
            .from(table.table)
            .where(table.scopeColumn.in(subScopesToDelete))
            .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
            .where(table.keyColumn.equalTo(key).or(table.keyColumn.startsWith(key + JsonDbConstants.PATH_SEPARATOR)))
            .execute();
      }
    }
  }

  @Override
  SubScopeDeleter getSelf() {
    return this;
  }

}
