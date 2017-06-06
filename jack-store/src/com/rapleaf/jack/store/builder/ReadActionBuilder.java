package com.rapleaf.jack.store.builder;

import com.rapleaf.jack.IDb;

public interface ReadActionBuilder<DB extends IDb, IMPL extends CreateActionBuilder<DB, ?>> {

  IMPL within(String scope);

}
