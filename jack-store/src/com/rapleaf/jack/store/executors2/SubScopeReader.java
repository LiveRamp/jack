package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbTuple;

public class SubScopeReader extends BaseReaderExecutor2<JsRecords, SubScopeReader> {

  static final boolean DEFAULT_SKIP_SUB_SCOPE_VALIDATION = false;

  private final Set<Long> subScopeIds = Sets.newHashSet();
  /**
   * This is only used by internal users, (e.g. {@link SubScopeUpdater}), that will always provide valid sub scope IDs.
   */
  private final boolean skipSubScopeValidation;
  private boolean ignoreInvalidSubScopes = false;

  SubScopeReader(JsTable table, boolean skipSubScopeValidation, Long executionScopeId) {
    super(table, executionScopeId);
    this.skipSubScopeValidation = skipSubScopeValidation;
  }

  @Override
  SubScopeReader getSelf() {
    return this;
  }

  public SubScopeReader whereSubScopeIds(Set<Long> subScopeIds) {
    this.subScopeIds.addAll(subScopeIds);
    return this;
  }

  public SubScopeReader ignoreInvalidSubScopes() {
    this.ignoreInvalidSubScopes = true;
    return this;
  }

  @Override
  public JsRecords execute(IDb db) throws IOException {
    Set<Long> validSubScopeIds;
    if (skipSubScopeValidation) {
      validSubScopeIds = subScopeIds;
    } else {
      validSubScopeIds = getValidSubScopeIds(db, subScopeIds, ignoreInvalidSubScopes);
    }

    if (validSubScopeIds.isEmpty()) {
      return JsRecords.empty(executionScopeId);
    }

    Records records = db.createQuery().from(table.table)
        .where(table.typeColumn.notEqualTo(ValueType.SCOPE.value))
        .where(table.scopeColumn.in(validSubScopeIds))
        .select(table.scopeColumn, table.typeColumn, table.keyColumn, table.valueColumn)
        .orderBy(table.scopeColumn)
        .orderBy(table.idColumn).fetch();

    if (records.isEmpty()) {
      List<JsRecord> emptyRecords = Lists.newLinkedList();
      for (long subScopeId : validSubScopeIds) {
        emptyRecords.add(JsRecord.empty(subScopeId));
      }
      return new JsRecords(executionScopeId, emptyRecords);
    }

    List<JsRecord> jsRecords = Lists.newLinkedList();

    Long previousScopeId = records.get(0).get(table.scopeColumn);
    Map<String, ValueType> types = Maps.newHashMap();
    Map<String, Object> values = Maps.newHashMap();
    List<JsonDbTuple> jsonTuples = Lists.newLinkedList();
    Set<String> jsonKeys = Sets.newHashSet();

    Iterator<Record> iterator = records.iterator();
    while (iterator.hasNext()) {
      Record record = iterator.next();
      Long currentScopeId = record.get(table.scopeColumn);

      if (!Objects.equals(previousScopeId, currentScopeId)) {
        // Scope ID changes
        // Construct a new record with previous entries
        addJsRecord(currentScopeId, types, values, jsonTuples, jsonKeys, jsRecords);

        types = Maps.newHashMap();
        values = Maps.newHashMap();
        jsonTuples = Lists.newLinkedList();
        jsonKeys = Sets.newHashSet();

        previousScopeId = currentScopeId;
      }

      appendRecord(types, values, jsonTuples, jsonKeys, record);

      if (!iterator.hasNext()) {
        // Construct the final record with previous entries
        addJsRecord(currentScopeId, types, values, jsonTuples, jsonKeys, jsRecords);
      }
    }

    return new JsRecords(executionScopeId, jsRecords);
  }

  private void addJsRecord(Long scopeId, Map<String, ValueType> types, Map<String, Object> values, List<JsonDbTuple> jsonTuples, Set<String> jsonKeys, List<JsRecord> jsRecords) {
    appendJsonRecord(types, values, jsonTuples, jsonKeys);
    if (!types.isEmpty()) {
      jsRecords.add(new JsRecord(scopeId, types, values));
    }
  }

}
