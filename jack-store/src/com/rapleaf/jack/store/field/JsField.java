package com.rapleaf.jack.store.field;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.executors.RecordIndexExecutor;

public interface JsField<T> {

  String getKey();

  BiFunction<RecordIndexExecutor, T, RecordIndexExecutor> getPutFunction();

  Function<JsRecord, T> getReadFunction();

}
