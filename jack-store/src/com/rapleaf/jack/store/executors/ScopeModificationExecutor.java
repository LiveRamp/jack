package com.rapleaf.jack.store.executors;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Joiner;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.store.exceptions.MissingScopeException;
import com.rapleaf.jack.transaction.ITransactor;

public class ScopeModificationExecutor<DB extends IDb> extends BaseExecutor<DB> {

  private final String currentName;
  private final String newName;

  ScopeModificationExecutor(ITransactor<DB> transactor, JsTable table, Optional<JsScope> predefinedScope, List<String> predefinedScopeNames, String currentName, String newName) {
    super(transactor, table, predefinedScope, predefinedScopeNames);
    this.currentName = currentName;
    this.newName = newName;
  }

  public boolean execute() {
    Optional<JsScope> executionScope = getOptionalExecutionScope();
    if (!executionScope.isPresent()) {
      throw new MissingScopeException(Joiner.on("/").join(predefinedScopeNames));
    }

    validateOperation(executionScope.get());

    Long upperScopeId = executionScope.get().getScopeId();
    return transactor.query(db ->
        db.createUpdate().table(table.table)
            .set(table.valueColumn, newName)
            .where(table.scopeColumn.as(Long.class).equalTo(upperScopeId))
            .where(table.keyColumn.equalTo(JsConstants.SCOPE_KEY))
            .where(table.typeColumn.equalTo(JsConstants.SCOPE_TYPE))
            .execute()
            .getUpdatedRowCount() == 1
    );
  }

  private void validateOperation(JsScope executionScope) {
    Optional<JsScope> currentScope = getOptionalScope(executionScope, Collections.singletonList(currentName));
    if (!currentScope.isPresent()) {
      throw new MissingScopeException(currentName);
    }

    Optional<JsScope> newScope = getOptionalScope(executionScope, Collections.singletonList(newName));
    if (newScope.isPresent()) {
      throw new JackRuntimeException(String.format("Scope %s already exists", newName));
    }
  }

}
