package com.rapleaf.jack.store;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.builder.CreateActionBuilder;
import com.rapleaf.jack.store.builder.DeleteActionBuilder;
import com.rapleaf.jack.store.builder.ReadActionBuilder;
import com.rapleaf.jack.store.builder.UpdateActionBuilder;

public interface StoreClient<DB extends IDb> {

  ScopedStoreClient<DB, ?> inScope(String scope);

}
