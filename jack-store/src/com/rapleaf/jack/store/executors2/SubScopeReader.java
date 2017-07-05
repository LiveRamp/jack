package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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

  private final Set<Long> subScopeIds = Sets.newHashSet();
  private boolean ignoreInvalidSubScopes = false;

  SubScopeReader(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  public SubScopeReader whereSubScopeIds(Collection<Long> subScopeIds) {
    this.subScopeIds.addAll(subScopeIds);
    return this;
  }

  public SubScopeReader ignoreInvalidSubScopes() {
    this.ignoreInvalidSubScopes = true;
    return this;
  }

  @Override
  public JsRecords execute(IDb db) throws IOException {
    Set<Long> validSubScopeIds = InternalScopeGetter.getValidSubScopeIds(db, table, executionScopeId, subScopeIds, ignoreInvalidSubScopes);
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
          jsRecords.add(recordCreator.createNewRecord(currentScopeId));
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
