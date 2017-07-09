package com.rapleaf.jack.store.field;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.executors.RecordUpdater;
import com.rapleaf.jack.store.executors.SubRecordUpdater;
import com.rapleaf.jack.store.iface.InsertValue;
import com.rapleaf.jack.store.iface.ValueIndexer;

public abstract class AbstractJsField<T> implements JsField<T> {

  private final String key;
  private final BiFunction<ValueIndexer, T, ValueIndexer> putFunction;
  private final Function<JsRecord, T> readFunction;

  AbstractJsField(String key, InsertValue<T> putMethod, BiFunction<JsRecord, String, T> getMethod) {
    this.key = key;
    this.putFunction = (recordUpdater, value) -> putMethod.apply(recordUpdater, key, value);
    this.readFunction = record -> getMethod.apply(record, key);
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public BiFunction<ValueIndexer, T, ValueIndexer> getPutFunction() {
    return putFunction;
  }

  @Override
  public Function<JsRecord, T> getReadFunction() {
    return readFunction;
  }

}
