package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

public class SubRecordReader extends BaseInquirerExecutor<JsRecords, JsRecords, SubRecordReader> {

  private Optional<Set<Long>> subRecordIds = Optional.empty();
  private boolean skipSubRecordIdValidation = false;

  SubRecordReader(JsTable table, Long executionRecordId) {
    super(table, executionRecordId);
  }

  // For internal use only, when the supplied sub scope IDs are guaranteed to be valid.
  SubRecordReader(JsTable table, Long executionRecordId, Collection<Long> subRecordIds) {
    super(table, executionRecordId);
    this.subRecordIds = Optional.of(Sets.newHashSet(subRecordIds));
    this.skipSubRecordIdValidation = true;
  }

  public SubRecordReader whereSubRecordIds(Collection<Long> subRecordIds) {
    if (this.subRecordIds.isPresent()) {
      this.subRecordIds.get().addAll(subRecordIds);
    } else {
      this.subRecordIds = Optional.of(Sets.newHashSet(subRecordIds));
    }
    return this;
  }

  public SubRecordReader whereSubRecordIds(Long subRecordId, Long... moreSubRecordIds) {
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

  @Override
  JsRecords internalExecute(IDb db) throws IOException {
    return internalExec(db);
  }

  @Override
  JsRecords internalExec(IDb db) throws IOException {
    Set<Long> validSubRecordIds;
    if (skipSubRecordIdValidation && subRecordIds.isPresent()) {
      validSubRecordIds = subRecordIds.get();
    } else {
      validSubRecordIds = InternalScopeGetter.getValidSubRecordIds(db, table, executionRecordId, subRecordIds);
    }
    if (validSubRecordIds.isEmpty()) {
      return JsRecords.empty(executionRecordId);
    }

    Records records = db.createQuery().from(table.table)
        .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
        .where(table.scopeColumn.in(validSubRecordIds))
        .select(table.scopeColumn, table.typeColumn, table.keyColumn, table.valueColumn)
        .orderBy(table.scopeColumn)
        // records must be sorted by ID so that json entries are read back in the exact same order as they are written to db
        .orderBy(table.idColumn)
        .fetch();

    if (records.isEmpty()) {
      List<JsRecord> emptyRecords = Lists.newLinkedList();
      for (long subRecordId : validSubRecordIds) {
        emptyRecords.add(JsRecord.empty(subRecordId));
      }
      return new JsRecords(executionRecordId, emptyRecords);
    }

    List<JsRecord> jsRecords = Lists.newLinkedList();

    Long previousRecordId = records.get(0).get(table.scopeColumn);
    InternalRecordCreator recordCreator = new InternalRecordCreator(table, selectedKeys);

    Iterator<Record> iterator = records.iterator();
    while (iterator.hasNext()) {
      Record record = iterator.next();
      Long currentRecordId = record.get(table.scopeColumn);

      if (!Objects.equals(previousRecordId, currentRecordId)) {
        // Scope ID changes
        // Construct a new record with previous entries
        if (recordCreator.hasNewRecord()) {
          jsRecords.add(recordCreator.createNewRecord(previousRecordId));
        }
        previousRecordId = currentRecordId;
      }

      recordCreator.appendRecord(record);

      if (!iterator.hasNext() && recordCreator.hasNewRecord()) {
        // Construct the final record with previous entries
        jsRecords.add(recordCreator.createNewRecord(currentRecordId));
      }
    }

    return new JsRecords(executionRecordId, jsRecords);
  }

  @Override
  SubRecordReader getSelf() {
    return this;
  }

}
