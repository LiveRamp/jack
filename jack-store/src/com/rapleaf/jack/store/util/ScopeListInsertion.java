package com.rapleaf.jack.store.util;

import java.util.List;

import com.rapleaf.jack.store.executors2.ScopeUpdater;

@FunctionalInterface
public interface ScopeListInsertion<T> extends ScopeValueInsertion<List<T>> {

  ScopeUpdater apply(ScopeUpdater executor, String key, List<T> value);

}
