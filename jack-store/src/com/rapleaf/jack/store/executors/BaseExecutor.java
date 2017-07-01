package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.ValueType;

public abstract class BaseExecutor {

  protected final JsTable table;
  protected final Optional<JsScope> predefinedScope;
  protected final List<String> predefinedScopeNames;

  BaseExecutor(JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    this.table = table;
    this.predefinedScope = predefinedScope;
    this.predefinedScopeNames = predefinedScopeNames;
  }

  JsScope getOrCreateExecutionScope(IDb db) throws IOException {
    return predefinedScope.orElse(getOrCreateScope(db, JsConstants.ROOT_SCOPE, predefinedScopeNames));
  }

  Optional<JsScope> getOptionalExecutionScope(IDb db) throws IOException {
    if (predefinedScope.isPresent()) {
      return predefinedScope;
    } else {
      return getOptionalScope(db, JsConstants.ROOT_SCOPE, predefinedScopeNames);
    }
  }

  JsScope getOrCreateScope(IDb db, JsScope executionScope, List<String> scopes) throws IOException {
    JsScope upperScope = executionScope;
    for (String scope : scopes) {
      Optional<JsScope> currentScope = getOptionalScope(db, upperScope, scope);
      if (currentScope.isPresent()) {
        upperScope = currentScope.get();
      } else {
        upperScope = createScope(db, upperScope, scope);
      }
    }
    return upperScope;
  }

  Optional<JsScope> getOptionalScope(IDb db, JsScope executionScope, List<String> scopes) throws IOException {
    JsScope upperScope = executionScope;
    for (String scope : scopes) {
      Optional<JsScope> currentScope = getOptionalScope(db, upperScope, scope);
      if (currentScope.isPresent()) {
        upperScope = currentScope.get();
      } else {
        return Optional.empty();
      }
    }
    return Optional.of(upperScope);
  }

  private JsScope createScope(IDb db, JsScope executionScope, String childScope) throws IOException {
    Long upperScopeId = executionScope.getScopeId();
    long childScopeId = db.createInsertion().into(table.table)
        .set(table.scopeColumn, upperScopeId)
        .set(table.keyColumn, JsConstants.SCOPE_KEY)
        .set(table.typeColumn, ValueType.SCOPE.value)
        .set(table.valueColumn, childScope)
        .execute()
        .getFirstId();
    return new JsScope(executionScope.getScopeId(), childScopeId, childScope);
  }

  private Optional<JsScope> getOptionalScope(IDb db, JsScope executionScope, String childScope) throws IOException {
    List<Long> ids = db.createQuery().from(table.table)
        .where(table.scopeColumn.equalTo(executionScope.getScopeId()))
        .where(table.keyColumn.equalTo(JsConstants.SCOPE_KEY))
        .where(table.typeColumn.equalTo(ValueType.SCOPE.value))
        .where(table.valueColumn.equalTo(childScope))
        .fetch()
        .gets(table.idColumn);

    if (ids.size() == 0) {
      return Optional.empty();
    } else if (ids.size() == 1) {
      return Optional.of(new JsScope(executionScope.getScopeId(), ids.get(0), childScope));
    } else {
      throw new JackRuntimeException(String.format("Duplicated scopes, %s, exist under parent scope %s", childScope, executionScope.toString()));
    }
  }

}
