package com.rapleaf.jack.store.builder;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.transaction.ITransactor;

public class CreateActionBuilderImpl<DB extends IDb> extends AbstractActionBuilder<DB, CreateActionBuilder<DB, ?>> implements CreateActionBuilder<DB, CreateActionBuilder<DB, ?>> {

  public CreateActionBuilderImpl(ITransactor<DB> transactor) {
    super(transactor);
  }

}
