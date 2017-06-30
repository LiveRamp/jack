package com.rapleaf.jack.store.util;

import com.rapleaf.jack.store.executors.RecordIndexExecutor;

@FunctionalInterface
public interface IndexValue<T> {

  RecordIndexExecutor apply(RecordIndexExecutor executor, String key, T value);

}
