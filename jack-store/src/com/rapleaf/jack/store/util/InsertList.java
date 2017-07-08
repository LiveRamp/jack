package com.rapleaf.jack.store.util;

import java.util.List;

import com.rapleaf.jack.store.executors.RecordUpdater;

@FunctionalInterface
public interface InsertList<T> extends InsertValue<List<T>> {

  RecordUpdater apply(RecordUpdater executor, String key, List<T> value);

}
