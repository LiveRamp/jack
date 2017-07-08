package com.rapleaf.jack.store.executors2;

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

public class SubScopeReader extends BaseInquirerExecutor2<JsRecords, SubScopeReader> {

  private Optional<Set<Long>> subScopeIds = Optional.empty();
  private boolean skipSubScopeIdValidation = false;

  SubScopeReader(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  // For internal use only, when the supplied sub scope IDs are guaranteed to be valid.
  SubScopeReader(JsTable table, Long executionScopeId, Collection<Long> subScopeIds) {
    super(table, executionScopeId);
    this.subScopeIds = Optional.of(Sets.newHashSet(subScopeIds));
    this.skipSubScopeIdValidation = true;
  }

  public SubScopeReader whereSubScopeIds(Collection<Long> subScopeIds) {
    if (this.subScopeIds.isPresent()) {
      this.subScopeIds.get().addAll(subScopeIds);
    } else {
      this.subScopeIds = Optional.of(Sets.newHashSet(subScopeIds));
    }
    return this;
  }

  public SubScopeReader whereSubScopeIds(Long subScopeId, Long... moreSubScopeIds) {
    if (this.subScopeIds.isPresent()) {
      this.subScopeIds.get().add(subScopeId);
      this.subScopeIds.get().addAll(Arrays.asList(moreSubScopeIds));
    } else {
      Set<Long> subScopeIds = Sets.newHashSet(subScopeId);
      subScopeIds.addAll(Arrays.asList(moreSubScopeIds));
      this.subScopeIds = Optional.of(subScopeIds);
    }
    return this;
  }

  @Override
  JsRecords internalExecute(IDb db) throws IOException {
    Set<Long> validSubScopeIds;
    if (skipSubScopeIdValidation && subScopeIds.isPresent()) {
      validSubScopeIds = subScopeIds.get();
    } else {
      validSubScopeIds = InternalScopeGetter.getValidSubScopeIds(db, table, executionScopeId, subScopeIds);
    }
    if (validSubScopeIds.isEmpty()) {
      return JsRecords.empty(executionScopeId);
    }

    Records records = db.createQuery().from(table.table)
        .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
        .where(table.scopeColumn.in(validSubScopeIds))
        .select(table.scopeColumn, table.typeColumn, table.keyColumn, table.valueColumn)
        .orderBy(table.scopeColumn)
        .orderBy(table.idColumn)
        .fetch();

    if (records.isEmpty()) {
      List<JsRecord> emptyRecords = Lists.newLinkedList();
      for (long subScopeId : validSubScopeIds) {
        emptyRecords.add(JsRecord.empty(subScopeId));
      }
      return new JsRecords(executionScopeId, emptyRecords);
    }

    List<JsRecord> jsRecords = Lists.newLinkedList();

    Long previousScopeId = records.get(0).get(table.scopeColumn);
    InternalRecordCreator recordCreator = new InternalRecordCreator(table, selectedKeys);

    Iterator<Record> iterator = records.iterator();
    while (iterator.hasNext()) {
      Record record = iterator.next();
      Long currentScopeId = record.get(table.scopeColumn);

      if (!Objects.equals(previousScopeId, currentScopeId)) {
        // Scope ID changes
        // Construct a new record with previous entries
        if (recordCreator.hasNewRecord()) {
          jsRecords.add(recordCreator.createNewRecord(previousScopeId));
        }
        previousScopeId = currentScopeId;
      }

      recordCreator.appendRecord(record);

      if (!iterator.hasNext() && recordCreator.hasNewRecord()) {
        // Construct the final record with previous entries
        jsRecords.add(recordCreator.createNewRecord(currentScopeId));
      }
    }

    return new JsRecords(executionScopeId, jsRecords);
  }

  @Override
  SubScopeReader getSelf() {
    return this;
  }

}
