package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Deletions;
import com.rapleaf.jack.queries.GenericDeletion;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

abstract class BaseDeleterExecutor<TF, TL, E extends BaseDeleterExecutor<TF, TL, E>> extends BaseExecutor<TF, TL> {

  final Set<String> keysToDelete = Sets.newHashSet();
  boolean deleteAllKeys = false;
  boolean deleteEntireRecord = false;
  boolean allowRecursion = false;

  BaseDeleterExecutor(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
  }

  abstract E getSelf();

  public E deleteKey(String keyToDelete, String... keysToDelete) {
    this.keysToDelete.add(keyToDelete);
    this.keysToDelete.addAll(Arrays.asList(keysToDelete));
    return getSelf();
  }

  public E deleteKey(Collection<String> keysToDelete) {
    this.keysToDelete.addAll(keysToDelete);
    return getSelf();
  }

  public E deleteAllKeys() {
    this.deleteAllKeys = true;
    return getSelf();
  }

  public E deleteEntireRecord() {
    this.deleteEntireRecord = true;
    return getSelf();
  }

  public E deleteEntireRecord(boolean deleteNestedRecords) {
    this.deleteEntireRecord = true;
    this.allowRecursion = deleteNestedRecords;
    return getSelf();
  }

  void deleteScopes(IDb db, Collection<Long> recordIds) throws IOException {
    Set<Long> nonNullRecordIds = recordIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());

    // delete records
    GenericDeletion recordDeletion = db.createDeletion().from(table.table);
    if (recordIds.contains(null)) {
      recordDeletion.where(table.scope.in(nonNullRecordIds).or(table.scope.isNull()));
    } else {
      recordDeletion.where(table.scope.in(recordIds));
    }
    recordDeletion.where(table.type.notEqualTo(ValueType.SCOPE.value)).execute();

    // delete scopes
    GenericDeletion scopeDeletion = db.createDeletion().from(table.table);
    if (recordIds.contains(null)) {
      scopeDeletion.where(table.id.in(nonNullRecordIds).or(table.id.isNull()));
    } else {
      scopeDeletion.where(table.id.in(recordIds));
    }
    Deletions deletionResult = scopeDeletion.where(table.type.equalTo(ValueType.SCOPE.value)).execute();

    long expectedDeleteCount = recordIds.contains(null) ? recordIds.size() - 1 : recordIds.size();
    Preconditions.checkState(
        deletionResult.getDeletedRowCount() == expectedDeleteCount,
        "Expect to delete %s scopes, but %s scopes are affected",
        expectedDeleteCount, deletionResult.getDeletedRowCount()
    );
  }

}
