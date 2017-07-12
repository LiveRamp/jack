package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.BulkOperationException;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;

public class SubRecordUpdater extends BaseCreatorExecutor<JsRecords, Set<Long>, SubRecordUpdater> {

  private Optional<Set<Long>> subRecordIds = Optional.empty();
  private boolean allowBulkUpdate = false;

  SubRecordUpdater(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
  }

  public SubRecordUpdater whereSubRecordIds(Set<Long> subRecordIds) {
    if (this.subRecordIds.isPresent()) {
      this.subRecordIds.get().addAll(subRecordIds);
    } else {
      this.subRecordIds = Optional.of(Sets.newHashSet(subRecordIds));
    }
    return this;
  }

  public SubRecordUpdater whereSubRecordIds(Long subRecordId, Long... moreSubRecordIds) {
    if (this.subRecordIds.isPresent()) {
      this.subRecordIds.get().add(subRecordId);
      this.subRecordIds.get().addAll(Arrays.asList(moreSubRecordIds));
    } else {
      Set<Long> subRecordIds = Sets.newHashSet(subRecordId);
      subRecordIds.addAll(Arrays.asList(moreSubRecordIds));
      this.subRecordIds = Optional.of(subRecordIds);
    }
    return this;
  }

  public SubRecordUpdater allowBulkUpdate() {
    this.allowBulkUpdate = true;
    return this;
  }

  @Override
  JsRecords internalExecute(IDb db) throws IOException {
    Set<Long> validSubRecordIds = internalExec(db);
    return new SubRecordReader(table, executionRecordId, validSubRecordIds, Collections.emptySet()).internalExecute(db);
  }

  @Override
  Set<Long> internalExec(IDb db) throws IOException {
    if (types.isEmpty()) {
      return Collections.emptySet();
    }

    if (!subRecordIds.isPresent() && !allowBulkUpdate) {
      throw new BulkOperationException("Bulk update is disabled; either enable it or specify at least one sub scope ID");
    }

    Set<Long> validSubRecordIds = InternalScopeGetter.getValidSubRecordIds(db, table, executionRecordId, subRecordIds);
    if (validSubRecordIds.isEmpty()) {
      return Collections.emptySet();
    }

    for (long subRecordId : validSubRecordIds) {
      deleteExistingEntries(db, subRecordId);
      insertNewEntries(db, subRecordId);
    }

    return validSubRecordIds;
  }

  @Override
  SubRecordUpdater getSelf() {
    return this;
  }

}
