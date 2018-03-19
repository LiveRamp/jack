package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.BulkOperationException;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbConstants;

public class SubRecordDeleter extends BaseDeleterExecutor<Void, Void, SubRecordDeleter> {

  private Optional<Set<Long>> subRecordIds = Optional.empty();
  private boolean allowBulkDeletion;

  SubRecordDeleter(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
    this.allowBulkDeletion = false;
  }

  public SubRecordDeleter whereSubRecordIds(Collection<Long> subRecordIds) {
    if (this.subRecordIds.isPresent()) {
      this.subRecordIds.get().addAll(subRecordIds);
    } else {
      this.subRecordIds = Optional.of(Sets.newHashSet(subRecordIds));
    }
    return getSelf();
  }

  public SubRecordDeleter whereSubRecordIds(Long subRecordId, Long... moreSubRecordIds) {
    if (this.subRecordIds.isPresent()) {
      this.subRecordIds.get().add(subRecordId);
      this.subRecordIds.get().addAll(Arrays.asList(moreSubRecordIds));
    } else {
      Set<Long> subRecordIds = Sets.newHashSet(subRecordId);
      subRecordIds.addAll(Arrays.asList(moreSubRecordIds));
      this.subRecordIds = Optional.of(subRecordIds);
    }
    return getSelf();
  }

  public SubRecordDeleter allowBulkDeletion() {
    this.allowBulkDeletion = true;
    return getSelf();
  }

  Void internalExecute(IDb db) throws IOException {
    return internalExec(db);
  }

  @Override
  Void internalExec(IDb db) throws IOException {
    if (!allowBulkDeletion && !subRecordIds.isPresent()) {
      throw new BulkOperationException("Bulk deletion is disabled; either enable it or specify at least one sub scope ID");
    }

    Set<Long> validSubRecordIds = InternalScopeGetter.getValidSubRecordIds(db, table, executionRecordId, subRecordIds);
    if (deleteEntireRecord) {
      executeRecordDeletion(db, validSubRecordIds);
    } else {
      executeKeyDeletion(db, validSubRecordIds);
    }
    return null;
  }

  private void executeRecordDeletion(IDb db, Collection<Long> subScopesToDelete) throws IOException {
    Set<Long> nestedRecordIds = InternalScopeGetter.getNestedRecordIds(db, table, subScopesToDelete);
    if (!nestedRecordIds.isEmpty() && !allowRecursion) {
      throw new JackRuntimeException("There are nested scopes under the scopes to delete");
    }
    Set<Long> recordIdsToDelete = Sets.newHashSet(subScopesToDelete);
    recordIdsToDelete.addAll(nestedRecordIds);
    deleteScopes(db, recordIdsToDelete);
  }

  private void executeKeyDeletion(IDb db, Collection<Long> subScopesToDelete) throws IOException {
    if (deleteAllKeys) {
      db.createDeletion()
          .from(table.table)
          .where(table.scope.in(subScopesToDelete))
          .where(table.type.notEqualTo(ValueType.SCOPE.value))
          .execute();
    } else if (!keysToDelete.isEmpty()) {
      for (String key : keysToDelete) {
        db.createDeletion()
            .from(table.table)
            .where(table.scope.in(subScopesToDelete))
            .where(table.type.notEqualTo(ValueType.SCOPE.value))
            .where(table.key.equalTo(key).or(table.key.startsWith(key + JsonDbConstants.PATH_SEPARATOR)))
            .execute();
      }
    }
  }

  @Override
  SubRecordDeleter getSelf() {
    return this;
  }

}
