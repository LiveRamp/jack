package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.UUID;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

public class SubScopeCreator extends BaseCreatorExecutor2<JsRecord, SubScopeCreator> {

  private String scopeName = null;

  SubScopeCreator(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  public SubScopeCreator scopeName(String name) {
    Preconditions.checkArgument(name != null && !name.isEmpty(), "Scope name cannot be null or empty");
    this.scopeName = name;
    return this;
  }

  @Override
  public JsRecord execute(IDb db) throws IOException {
    Long scopeId = createNewScope(db);
    if (!types.isEmpty()) {
      insertNewEntries(db, scopeId);
    }
    return new JsRecord(scopeId, types, values);
  }

  private Long createNewScope(IDb db) throws IOException {
    if (scopeName == null) {
      scopeName = UUID.randomUUID().toString();
    }
    return db.createInsertion()
        .into(table.table)
        .set(table.scopeColumn, executionScopeId)
        .set(table.keyColumn, JsConstants.SCOPE_KEY)
        .set(table.typeColumn, ValueType.SCOPE.value)
        .set(table.valueColumn, scopeName)
        .execute()
        .getFirstId();
  }

  @Override
  SubScopeCreator getSelf() {
    return this;
  }

}
