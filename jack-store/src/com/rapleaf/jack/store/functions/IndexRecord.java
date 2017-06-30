package com.rapleaf.jack.store.functions;

import com.rapleaf.jack.store.executors.RecordIndexExecutor;

@FunctionalInterface
public interface IndexRecord<VALUE> {

  RecordIndexExecutor apply(RecordIndexExecutor executor, String key, VALUE value);

}
