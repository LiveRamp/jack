package com.rapleaf.jack.store.field;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.executors2.ScopeUpdater;

public interface JsField<T> {

  String getKey();

  BiFunction<ScopeUpdater, T, ScopeUpdater> getPutFunction();

  Function<JsRecord, T> getReadFunction();

}
