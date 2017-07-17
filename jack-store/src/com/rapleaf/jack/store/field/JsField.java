package com.rapleaf.jack.store.field;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.iface.ValueIndexer;

public interface JsField<T> {

  String getKey();

  BiFunction<ValueIndexer, T, ValueIndexer> getPutFunction();

  Function<JsRecord, T> getReadFunction();

}
