package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.queries.Record;
import com.rapleaf.jack.queries.Records;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsRecords;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;
import com.rapleaf.jack.store.json.JsonDbTuple;

public abstract class BaseExecutor2<T> {

  protected final JsTable table;
  protected final Long executionScopeId;

  public BaseExecutor2(JsTable table, Long executionScopeId) {
    this.table = table;
    this.executionScopeId = executionScopeId;
  }

  abstract public T execute(IDb db) throws IOException;

  Set<Long> getValidSubScopeIds(IDb db, Set<Long> subScopeIds, boolean ignoreInvalidSubScopes) throws IOException {
    Set<Long> validSubScopeIds;
    if (subScopeIds.isEmpty()) {
      validSubScopeIds = getAllSubScopeIds(db);
    } else {
      validSubScopeIds = getValidSubScopeIds(db, subScopeIds);
    }

    if (!validSubScopeIds.equals(subScopeIds) && !ignoreInvalidSubScopes) {
      throw new JackRuntimeException(String.format(
          "Sub scopes %s does not exist under scope %s; either ignore invalid sub scopes or provide valid sub scope IDs",
          Joiner.on(", ").join(Sets.difference(subScopeIds, validSubScopeIds)), executionScopeId == null ? JsConstants.ROOT_SCOPE_NAME : executionScopeId
      ));
    }

    return validSubScopeIds;
  }

  private Set<Long> getAllSubScopeIds(IDb db) throws IOException {
    return Sets.newHashSet(
        db.createQuery().from(table.table)
            .where(table.scopeColumn.equalTo(executionScopeId))
            .where(table.typeColumn.equalTo(ValueType.SCOPE.value))
            .select(table.idColumn)
            .fetch()
            .gets(table.idColumn)
    );
  }

  private Set<Long> getValidSubScopeIds(IDb db, Set<Long> subScopeIds) throws IOException {
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
