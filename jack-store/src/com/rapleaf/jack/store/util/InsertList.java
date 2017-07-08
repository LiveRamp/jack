package com.rapleaf.jack.store.util;

import java.util.List;

import com.rapleaf.jack.store.executors.ScopeUpdater;

@FunctionalInterface
public interface InsertList<T> extends InsertValue<List<T>> {

  ScopeUpdater apply(ScopeUpdater executor, String key, List<T> value);

}
