package com.rapleaf.jack.store.field;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.executors.RecordUpdater;

public interface JsField<T> {

  String getKey();

  BiFunction<RecordUpdater, T, RecordUpdater> getPutFunction();

  Function<JsRecord, T> getReadFunction();

}
