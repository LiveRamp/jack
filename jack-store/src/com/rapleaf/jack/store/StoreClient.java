package com.rapleaf.jack.store;

import com.rapleaf.jack.IDb;

public interface StoreClient<DB extends IDb> {

  ScopedStoreClient<DB, ?> inScope(String scope);

}
