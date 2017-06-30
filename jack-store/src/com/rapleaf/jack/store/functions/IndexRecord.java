package com.rapleaf.jack.store.functions;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.executors.RecordIndexExecutor;

@FunctionalInterface
public interface IndexRecord<DB extends IDb, VALUE> {

  RecordIndexExecutor<DB> apply(RecordIndexExecutor<DB> executor, String key, VALUE value);

}
