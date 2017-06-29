package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.Optional;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.transaction.ITransactor;

public abstract class BaseExecutor<DB extends IDb> {

  protected final ITransactor<DB> transactor;
  protected final JsTable table;
  protected final Optional<JsScope> predefinedScope;
  protected final List<String> predefinedScopeNames;

  BaseExecutor(ITransactor<DB> transactor, JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames) {
    this.transactor = transactor;
    this.table = table;
    this.predefinedScope = predefinedScope;
    this.predefinedScopeNames = predefinedScopeNames;
  }

  JsScope getOrCreateExecutionScope() {
    return predefinedScope.orElse(getOrCreateScope(JsConstants.ROOT_SCOPE, predefinedScopeNames));
  }

  Optional<JsScope> getOptionalExecutionScope() {
    if (predefinedScope.isPresent()) {
      return predefinedScope;
    } else {
      return getOptionalScope(JsConstants.ROOT_SCOPE, predefinedScopeNames);
    }
  }

  JsScope getOrCreateScope(JsScope executionScope, List<String> scopes) {
    JsScope upperScope = executionScope;
    for (String scope : scopes) {
      Optional<JsScope> currentScope = getOptionalScope(upperScope, scope);
      if (currentScope.isPresent()) {
        upperScope = currentScope.get();
      } else {
        upperScope = createScope(upperScope, scope);
      }
    }
    return upperScope;
  }

  Optional<JsScope> getOptionalScope(JsScope executionScope, List<String> scopes) {
    JsScope upperScope = executionScope;
    for (String scope : scopes) {
      Optional<JsScope> currentScope = getOptionalScope(upperScope, scope);
      if (currentScope.isPresent()) {
        upperScope = currentScope.get();
      } else {
        return Optional.empty();
      }
    }
    return Optional.of(upperScope);
  }

  private JsScope createScope(JsScope executionScope, String childScope) {
    Long upperScopeId = executionScope.getScopeId();
    long childScopeId = transactor.queryAsTransaction(db ->
        db.createInsertion().into(table.table)
            .set(table.scopeColumn, upperScopeId)
            .set(table.keyColumn, JsConstants.SCOPE_KEY)
            .set(table.typeColumn, JsConstants.SCOPE_TYPE)
            .set(table.valueColumn, childScope)
            .execute()
            .getFirstId()
    );
    return new JsScope(childScopeId, childScope);
  }

  private Optional<JsScope> getOptionalScope(JsScope executionScope, String childScope) {
    List<Long> ids = transactor.query(db ->
        db.createQuery().from(table.table)
            .where(table.scopeColumn.equalTo(executionScope.getScopeId()))
            .where(table.keyColumn.equalTo(JsConstants.SCOPE_KEY))
            .where(table.typeColumn.equalTo(JsConstants.SCOPE_TYPE))
            .where(table.valueColumn.equalTo(childScope))
            .fetch()
            .gets(table.idColumn)
    );

    if (ids.size() == 0) {
      return Optional.empty();
    } else if (ids.size() == 1) {
      return Optional.of(new JsScope(ids.get(0), childScope));
    } else {
      throw new JackRuntimeException(String.format("Duplicated scopes, %s, exist under parent scope %s", childScope, executionScope.toString()));
    }
  }

}
