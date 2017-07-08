package com.rapleaf.jack.store.util;

import com.rapleaf.jack.store.executors2.ScopeUpdater;

@FunctionalInterface
public interface InsertValue<T> {

  ScopeUpdater apply(ScopeUpdater executor, String key, T value);

}
