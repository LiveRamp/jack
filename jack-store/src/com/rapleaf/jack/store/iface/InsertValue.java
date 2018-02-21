package com.rapleaf.jack.store.iface;

@FunctionalInterface
public interface InsertValue<T> {

  ValueIndexer apply(ValueIndexer executor, String key, T value);

}
