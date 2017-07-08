package com.rapleaf.jack.store.field;

import java.util.function.BiFunction;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.util.InsertValue;

public class JsValueField<T> extends AbstractJsField<T> {

  JsValueField(String key, InsertValue<T> putMethod, BiFunction<JsRecord, String, T> getMethod) {
    super(key, putMethod, getMethod);

  }

}
