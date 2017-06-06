package com.rapleaf.jack.store;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.queries.Column;
import com.rapleaf.jack.queries.Table;
import com.rapleaf.jack.transaction.ITransactor;

public class StoreClientImpl<DB extends IDb> implements StoreClient<DB> {

  private final ITransactor<DB> transactor;
  private final Table<?, ?> table;
  private final Column<String> scopeColumn;
  private final Column<String> keyColumn;
  private final Column<String> typeColumn;
  private final Column<String> valueColumn;

  public StoreClientImpl(ITransactor<DB> transactor, Table<?, ?> table, Column<String> scopeColumn, Column<String> keyColumn, Column<String> typeColumn, Column<String> valueColumn) {
    this.transactor = transactor;
    this.table = table;
    this.scopeColumn = scopeColumn;
    this.keyColumn = keyColumn;
    this.typeColumn = typeColumn;
    this.valueColumn = valueColumn;
  }

  @Override
  public ScopedStoreClientImpl<DB> inScope(String scope) {
    transactor.execute(db -> {
      db.create
    });
    return new ScopedStoreClientImpl<>(transactor, scope);
  }

}
