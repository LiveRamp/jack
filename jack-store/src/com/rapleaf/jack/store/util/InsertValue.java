package com.rapleaf.jack.store.util;

import com.rapleaf.jack.store.executors.RecordUpdater;

@FunctionalInterface
public interface InsertValue<T> {

  RecordUpdater apply(RecordUpdater executor, String key, T value);

}
