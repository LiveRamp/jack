package com.rapleaf.jack.store.builder;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.transaction.ITransactor;

public abstract class AbstractActionBuilder<DB extends IDb, IMPL extends ActionBuilder<DB, ?>> implements ActionBuilder<DB, IMPL> {

  protected final ITransactor<DB> transactor;

  protected AbstractActionBuilder(ITransactor<DB> transactor) {
    this.transactor = transactor;
  }
}
