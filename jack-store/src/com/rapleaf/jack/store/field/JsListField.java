package com.rapleaf.jack.store.field;

import java.util.List;
import java.util.function.BiFunction;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.iface.InsertList;

public class JsListField<T> extends AbstractJsField<List<T>> {

  JsListField(String key, InsertList<T> putListMethod, BiFunction<JsRecord, String, List<T>> getListMethod) {
    super(key, putListMethod, getListMethod);
  }

}
