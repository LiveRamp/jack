package com.rapleaf.jack.store.field;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.rapleaf.jack.store.JsRecord;
import com.rapleaf.jack.store.util.IndexValue;

/**
 * Convert enum to name using {@link Enum#name}
 * Convert name to enum using {@link Enum#valueOf}
 */
public class JsEnumNameField<E extends Enum<E>> extends AbstractJsField<E> {

  JsEnumNameField(String key, Function<String, E> nameToEnumFunction, IndexValue<String> putMethod, BiFunction<JsRecord, String, String> getMethod) {
    super(key, convertPutMethod(putMethod), convertGetMethod(nameToEnumFunction, getMethod));
  }

  private static <E extends Enum> IndexValue<E> convertPutMethod(IndexValue<String> putMethod) {
    return (executor, key, value) -> putMethod.apply(executor, key, value.name());
  }

  private static <E extends Enum<E>> BiFunction<JsRecord, String, E> convertGetMethod(Function<String, E> nameToEnumFunction, BiFunction<JsRecord, String, String> getMethod) {
    return (record, key) -> {
      String enumName = getMethod.apply(record, key);
      return nameToEnumFunction.apply(enumName);
    };
  }

}
