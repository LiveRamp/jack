package com.rapleaf.jack.store.functions;

import java.util.List;

import com.rapleaf.jack.IDb;
import com.rapleaf.jack.store.executors.RecordIndexExecutor;

@FunctionalInterface
public interface IndexListRecord<DB extends IDb, VALUE> {

  RecordIndexExecutor<DB> apply(RecordIndexExecutor<DB> executor, String key, List<VALUE> value);

}
