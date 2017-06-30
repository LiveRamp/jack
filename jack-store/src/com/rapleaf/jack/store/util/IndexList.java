package com.rapleaf.jack.store.util;

import java.util.List;

import com.rapleaf.jack.store.executors.RecordIndexExecutor;

@FunctionalInterface
public interface IndexList<T> extends IndexValue<List<T>> {

  RecordIndexExecutor apply(RecordIndexExecutor executor, String key, List<T> value);

}
