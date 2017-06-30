package com.rapleaf.jack.store.functions;

import java.util.List;

import com.rapleaf.jack.store.executors.RecordIndexExecutor;

@FunctionalInterface
public interface IndexListRecord<VALUE> {

  RecordIndexExecutor apply(RecordIndexExecutor executor, String key, List<VALUE> value);

}
