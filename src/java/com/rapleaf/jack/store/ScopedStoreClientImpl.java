package com.rapleaf.jack.store;

import java.util.List;

import com.google.common.collect.Lists;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.builder.CreateActionBuilder;
import com.rapleaf.jack.store.builder.DeleteActionBuilder;
import com.rapleaf.jack.store.builder.ReadActionBuilder;
import com.rapleaf.jack.store.builder.UpdateActionBuilder;
import com.rapleaf.jack.transaction.ITransactor;

public class ScopedStoreClientImpl<DB extends IDb> implements ScopedStoreClient<DB, ScopedStoreClientImpl<DB>> {

  private final ITransactor<DB> transactor;
  private final List<String> scopes;

  ScopedStoreClientImpl(ITransactor<DB> transactor, String scope) {
    this.transactor = transactor;
    this.scopes = Lists.newArrayList(scope);
  }

  @Override
  public ScopedStoreClientImpl<DB> inScope(String scope) {
    return null;
  }

  @Override
  public CreateActionBuilder<DB, ?> create() {
    return null;
  }

  @Override
  public ReadActionBuilder<DB, ?> read() {
    return null;
  }

  @Override
  public UpdateActionBuilder<DB, ?> update() {
    return null;
  }

  @Override
  public DeleteActionBuilder<DB, ?> delete() {
    return null;
  }

}
