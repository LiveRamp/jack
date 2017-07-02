package com.rapleaf.jack.store.executors2;

import java.io.IOException;
import java.util.UUID;

import com.google.common.base.Preconditions;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

public class SubScopeCreator extends BaseCreatorExecutor2<SubScopeCreator> {

  private String scopeName;

  SubScopeCreator(JsTable table, Long executionScopeId) {
    super(table, executionScopeId);
  }

  @Override
  SubScopeCreator getSelf() {
    return this;
  }

  @Override
  Long getScopeId(IDb db) throws IOException {
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

  public SubScopeCreator scopeName(String name) {
    Preconditions.checkArgument(name != null && !name.isEmpty(), "Scope name cannot be null or empty");
    this.scopeName = name;
    return this;
  }

}
