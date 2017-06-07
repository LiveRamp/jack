package com.rapleaf.jack.store.executors;

import java.util.List;
import java.util.Optional;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.exception.JackRuntimeException;
import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.Table;
import com.rapleaf.jack.store.JsConstants;
import com.rapleaf.jack.store.JsScope;
import com.rapleaf.jack.transaction.ITransactor;

public class JsBaseExecutor<DB extends IDb> {

  private final ITransactor<DB> transactor;
  private final Table<?, ?> table;
  private final Column<Long> idColumn;
  private final Column<String> scopeColumn;
  private final Column<String> typeColumn;
  private final Column<String> keyColumn;
  private final Column<String> valueColumn;

  public JsBaseExecutor(ITransactor<DB> transactor, Table<?, ?> table, Column<String> scopeColumn, Column<String> typeColumn, Column<String> keyColumn, Column<String> valueColumn) {
    this.transactor = transactor;
    this.table = table;
    this.idColumn = Column.fromId(table.getName());
    this.scopeColumn = scopeColumn;
    this.typeColumn = typeColumn;
    this.keyColumn = keyColumn;
    this.valueColumn = valueColumn;
  }

  public JsScope getOrCreateScope(JsScope parentScope, List<String> scopes) {
    JsScope upperScope = parentScope;
    for (String scope : scopes) {
      Optional<JsScope> currentScope = getScope(upperScope, scope);
      if (currentScope.isPresent()) {
        upperScope = currentScope.get();
      } else {
        upperScope = createScope(upperScope, scope);
      }
    }
    return upperScope;
  }

  public boolean renameScope(JsScope parentScope, String oldName, String newName) {
    Long upperScopeId = parentScope.getScopeId();
    Optional<JsScope> scope = getScope(parentScope, oldName);

    if (scope.isPresent()) {
      return transactor.query(db ->
          db.createUpdate().table(table)
              .set(valueColumn, newName)
              .where(scopeColumn.as(Long.class).equalTo(upperScopeId))
              .where(keyColumn.equalTo(JsConstants.SCOPE_KEY))
              .where(typeColumn.equalTo(JsConstants.SCOPE_TYPE))
              .execute()
              .getUpdatedRowCount() == 1
      );
    } else {
      return true;
    }
  }

  private Optional<JsScope> getScope(JsScope parentScope, String childScope) {
    List<Long> ids = transactor.query(db ->
        db.createQuery().from(table)
            .where(scopeColumn.as(Long.class).equalTo(parentScope.getScopeId()))
            .where(keyColumn.equalTo(JsConstants.SCOPE_KEY))
            .where(typeColumn.equalTo(JsConstants.SCOPE_TYPE))
            .where(valueColumn.equalTo(childScope))
            .fetch()
            .gets(idColumn)
    );

    if (ids.size() == 0) {
      return Optional.empty();
    } else if (ids.size() == 1) {
      return Optional.of(new JsScope(ids.get(0), childScope));
    } else {
      throw new JackRuntimeException(String.format("Duplicated scopes with name %s exist under parent scope %s", childScope, parentScope.toString()));
    }
  }

  private JsScope createScope(JsScope parentScope, String childScope) {
    Long upperScopeId = parentScope.getScopeId();
    long childScopeId = transactor.queryAsTransaction(db ->
        db.createInsertion().into(table)
            .set(scopeColumn.as(Long.class), upperScopeId)
            .set(keyColumn, JsConstants.SCOPE_KEY)
            .set(typeColumn, JsConstants.SCOPE_TYPE)
            .set(valueColumn, childScope)
            .execute()
            .getFirstId()
    );
    return new JsScope(childScopeId, childScope);
  }

}
