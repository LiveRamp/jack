package com.rapleaf.jack.store.builder;

import com.rapleaf.jack.IDb;

public interface CreateActionBuilder<DB extends IDb, IMPL extends CreateActionBuilder<DB, ?>> extends ActionBuilder<DB, IMPL> {
}
