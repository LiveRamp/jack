package com.rapleaf.jack.store.iface;

import java.util.List;

@FunctionalInterface
public interface InsertList<T> extends InsertValue<List<T>> {

  ValueIndexer apply(ValueIndexer executor, String key, List<T> value);

}
