package com.rapleaf.jack.store.executors;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Joiner;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.queries.GenericConstraint;
import com.rapleaf.jack.queries.where_operators.JackMatchers;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.JsScopes;
import com.rapleaf.jack.store.JsTable;
import com.rapleaf.jack.store.exceptions.DuplicatedScopeException;
import com.rapleaf.jack.store.exceptions.MissingScopeException;

/**
 * Get sub scopes under the execution scope
 */
public class SubScopeGetterExecutor extends BaseExecutor {

  private final String scope;
  private final GetterType getterType;

  enum GetterType {
    ID, NAME
  }

  SubScopeGetterExecutor(JsTable jsTable, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames, long scopeId) {
    super(jsTable, predefinedScope, predefinedScopeNames);
    this.scope = String.valueOf(scopeId);
    this.getterType = GetterType.ID;
  }

  SubScopeGetterExecutor(JsTable jsTable, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames, String scopeName) {
    super(jsTable, predefinedScope, predefinedScopeNames);
    this.scope = scopeName;
    this.getterType = GetterType.NAME;
  }

  public JsScope execute(IDb db) throws IOException {
    JsScopes scopes = getScopes(db);
    if (scopes.isEmpty()) {
      throw new MissingScopeException(getterType.name() + " " + scopes);
    }
    return scopes.getScopes().get(0);
  }

  public Optional<JsScope> getOptional(IDb db) throws IOException {
    JsScopes scopes = getScopes(db);
    if (scopes.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(scopes.getScopes().get(0));
    }
  }

  private JsScopes getScopes(IDb db) throws IOException {
    Optional<JsScope> executionScope = getOptionalExecutionScope(db);
    if (executionScope.isPresent()) {
      JsScopes scopes = SubScopeQueryExecutor.queryScope(db, table, executionScope.get(), Collections.singletonList(getConstraint()));
      if (scopes.isEmpty() || scopes.size() == 1) {
        return scopes;
      } else {
        throw new DuplicatedScopeException(getterType.name() + " " + scopes, executionScope.get());
      }
    } else {
      throw new MissingScopeException(Joiner.on("/").join(predefinedScopeNames));
    }
  }

  private GenericConstraint getConstraint() {
    switch (getterType) {
      case ID:
        return new GenericConstraint<>(table.idColumn.as(String.class), JackMatchers.equalTo(scope));
      case NAME:
        return new GenericConstraint<>(table.valueColumn, JackMatchers.equalTo(scope));
      default:
        throw new JackRuntimeException("Unexpected getter type: " + getterType.name());
    }
  }

}
