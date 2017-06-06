package com.rapleaf.jack.store;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.builder.CreateActionBuilder;
import com.rapleaf.jack.store.builder.DeleteActionBuilder;
import com.rapleaf.jack.store.builder.ReadActionBuilder;
import com.rapleaf.jack.store.builder.UpdateActionBuilder;

public interface ScopedStoreClient<DB extends IDb, IMPL extends ScopedStoreClient<DB, IMPL>> extends StoreClient<DB> {

  @Override
  IMPL inScope(String scope);

  CreateActionBuilder<DB, ?> create();

  ReadActionBuilder<DB, ?> read();

  UpdateActionBuilder<DB, ?> update();

  DeleteActionBuilder<DB, ?> delete();

}
